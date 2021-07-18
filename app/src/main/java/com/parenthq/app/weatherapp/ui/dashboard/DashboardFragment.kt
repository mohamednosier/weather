package com.parenthq.app.weatherapp.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.transition.TransitionInflater
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.parenthq.app.weatherapp.R
import com.parenthq.app.weatherapp.core.BaseFragment
import com.parenthq.app.weatherapp.core.Constants
import com.parenthq.app.weatherapp.databinding.FragmentDashboardBinding
import com.parenthq.app.weatherapp.di.Injectable
import com.parenthq.app.weatherapp.domain.model.ListItem
import com.parenthq.app.weatherapp.domain.usecase.CurrentWeatherUseCase
import com.parenthq.app.weatherapp.domain.usecase.ForecastUseCase
import com.parenthq.app.weatherapp.ui.dashboard.forecast.ForecastAdapter
import com.parenthq.app.weatherapp.ui.main.MainActivity
import com.parenthq.app.weatherapp.utils.extensions.isNetworkAvailable
import com.parenthq.app.weatherapp.utils.extensions.observeWith
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

//import com.google.android.gms.common.api.GoogleApiClient

class DashboardFragment : BaseFragment<DashboardFragmentViewModel, FragmentDashboardBinding>(R.layout.fragment_dashboard, DashboardFragmentViewModel::class.java), Injectable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//    internal var mGoogleApiClient: GoogleApiClient? = null

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest

    override fun init() {
        super.init()
        initForecastAdapter()
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        val lat: String? = binding.viewModel?.sharedPreferences?.getString(Constants.Coords.LAT, "")
        val lon: String? = binding.viewModel?.sharedPreferences?.getString(Constants.Coords.LON, "")
        Log.d(TAG, "init: "+lat)
        Log.d(TAG, "init: "+lon)
        if (lat?.isNotEmpty() == true && lon?.isNotEmpty() == true) {
            binding.viewModel?.setCurrentWeatherParams(CurrentWeatherUseCase.CurrentWeatherParams(lat, lon, isNetworkAvailable(requireContext()), Constants.Coords.METRIC))
            binding.viewModel?.setForecastParams(ForecastUseCase.ForecastParams(lat, lon, isNetworkAvailable(requireContext()), Constants.Coords.METRIC))
        }
        else{
//            if (checkPermissions()) {
                if (isLocationEnabled()) {


                    buildGoogleApiClient()

                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

//                    if (ActivityCompat.checkSelfPermission(
//                            context!!,
//                            Manifest.permission.ACCESS_FINE_LOCATION
//                        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                            context!!,
//                            Manifest.permission.ACCESS_COARSE_LOCATION
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {


                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M ) {
                        checkPermission1();
                    }
                    // Re-check before enabling. You can add an else statement to warn the user about the lack of functionality if it's disabled.
                    // "or" is used instead of "and" as per the error. If it requires both, flip it over to &&. (I'm not sure, I haven't used GPS stuff before)
                    if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context!!,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){



                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {

                        binding.viewModel?. saveCoordsToSharedPref((location.latitude).toString(),(location.latitude).toString())
                        binding.viewModel?.setCurrentWeatherParams(CurrentWeatherUseCase.CurrentWeatherParams(
                            (location.altitude).toString(), (location.longitude).toString(), isNetworkAvailable(requireContext()), Constants.Coords.METRIC))
                        binding.viewModel?.setForecastParams(ForecastUseCase.ForecastParams((location.altitude).toString(), (location.longitude).toString(), isNetworkAvailable(requireContext()), Constants.Coords.METRIC))

                    }

                }
            }


//                    buildGoogleApiClient()
//                    map!!.isMyLocationEnabled = true
//                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//                    mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
//                        var location: Location? = task.result
//                        if (location == null) {
//                            requestNewLocationData()
//                        } else {
//                            latitude= location.latitude
//                            longitude = location.longitude
//                            Log.d(ContentValues.TAG, "onMapReady: "+latitude+" "+longitude)
//                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
//                            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
//                            val obj = addresses[0]
//                            val homeLatLng = LatLng(latitude, longitude)
//                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
//                            map.addMarker(MarkerOptions().position(homeLatLng).title(""+obj.getAdminArea()))
//
//                            val googleOverlay = GroundOverlayOptions()
//                                .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_logo))
//                                .position(homeLatLng, overlaySize)
//                            //    map.addGroundOverlay(googleOverlay)
//
////        setMapLongClick(map)
//                            setPoiClick(map)
//                            setMapStyle(map)
//                            enableMyLocation()
                }

        else {
//                    checkPermission1()
            Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

}

binding.viewModel?.getForecastViewState()?.observeWith(
    viewLifecycleOwner
) {
    with(binding) {
        viewState = it
        it.data?.list?.let { forecasts -> initForecast(forecasts) }
        (activity as MainActivity).viewModel.toolbarTitle.set(it.data?.city?.getCityAndCountry())
    }
}

binding.viewModel?.getCurrentWeatherViewState()?.observeWith(
    viewLifecycleOwner
) {
    with(binding) {
        containerForecast.viewState = it
    }
}
}

private fun initForecastAdapter() {
val adapter = ForecastAdapter { item, cardView, forecastIcon, dayOfWeek, temp, tempMaxMin ->
    val action = DashboardFragmentDirections.actionDashboardFragmentToWeatherDetailFragment(item)
    findNavController()
        .navigate(
            action,
            FragmentNavigator.Extras.Builder()
                .addSharedElements(
                    mapOf(
                        cardView to cardView.transitionName,
                        forecastIcon to forecastIcon.transitionName,
                        dayOfWeek to dayOfWeek.transitionName,
                        temp to temp.transitionName,
                        tempMaxMin to tempMaxMin.transitionName
                    )
                )
                .build()
        )
}

binding.recyclerForecast.adapter = adapter
binding.recyclerForecast.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
postponeEnterTransition()
binding.recyclerForecast.viewTreeObserver
    .addOnPreDrawListener {
        startPostponedEnterTransition()
        true
    }
}

private fun initForecast(list: List<ListItem>) {
(binding.recyclerForecast.adapter as ForecastAdapter).submitList(list)
}

private fun isLocationEnabled(): Boolean {
var locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
    LocationManager.NETWORK_PROVIDER

)
}

private fun checkPermissions(): Boolean {
if (ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
    ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
) {
    return true
}
return false
}
fun checkPermission1() {
if (ContextCompat.checkSelfPermission(
        context!!,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED ||
    ContextCompat.checkSelfPermission(
        context!!,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED
) { //Can add more as per requirement
    ActivityCompat.requestPermissions(
        activity!!,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        123
    )
}
}

private fun requestPermissions() {
ActivityCompat.requestPermissions(
    requireActivity(),
    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
    42
)
}
@Synchronized
protected fun buildGoogleApiClient() {
mGoogleApiClient = GoogleApiClient.Builder(requireContext())
    .addConnectionCallbacks(this)
    .addOnConnectionFailedListener(this)
    .addApi(LocationServices.API).build()
mGoogleApiClient!!.connect()
}
@SuppressLint("MissingPermission")
private fun requestNewLocationData() {
var mLocationRequest = LocationRequest()
mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
mLocationRequest.interval = 0
mLocationRequest.fastestInterval = 0
mLocationRequest.numUpdates = 1

mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
mFusedLocationClient!!.requestLocationUpdates(
mLocationRequest, mLocationCallback,
Looper.myLooper()
)
}
private val mLocationCallback = object : LocationCallback() {
override fun onLocationResult(locationResult: LocationResult) {
    var mLastLocation: Location = locationResult.lastLocation
}
}
override fun onConnected(bundle: Bundle?) {

mLocationRequest = LocationRequest()
mLocationRequest.interval = 1000
mLocationRequest.fastestInterval = 1000
mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
if (ContextCompat.checkSelfPermission(requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
    LocationServices.getFusedLocationProviderClient(requireActivity())        }
}

override fun onConnectionSuspended(i: Int) {

}
override fun onConnectionFailed(connectionResult: ConnectionResult) {

}

    override fun onResume() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
//            binding.viewModel?. saveCoordsToSharedPref("51.5073","-0.127647")
//            binding.viewModel?.setCurrentWeatherParams(CurrentWeatherUseCase.CurrentWeatherParams(
//                "51.5073", "-0.127647", isNetworkAvailable(requireContext()), Constants.Coords.METRIC))
//            binding.viewModel?.setForecastParams(ForecastUseCase.ForecastParams("51.5073", "-0.127647", isNetworkAvailable(requireContext()), Constants.Coords.METRIC))
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                var location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {

                    binding.viewModel?. saveCoordsToSharedPref((location.latitude).toString(),(location.latitude).toString())
                    binding.viewModel?.setCurrentWeatherParams(CurrentWeatherUseCase.CurrentWeatherParams(
                        (location.altitude).toString(), (location.longitude).toString(), isNetworkAvailable(requireContext()), Constants.Coords.METRIC))
                    binding.viewModel?.setForecastParams(ForecastUseCase.ForecastParams((location.altitude).toString(), (location.longitude).toString(), isNetworkAvailable(requireContext()), Constants.Coords.METRIC))

                }

            }
        }else {
            checkPermission1()
        }
        super.onResume()
    }
}
