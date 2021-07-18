package com.parenthq.app.weatherapp.repo

import NetworkBoundResource
import androidx.lifecycle.LiveData
import com.parenthq.app.weatherapp.core.Constants.NetworkService.RATE_LIMITER_TYPE
import com.parenthq.app.weatherapp.db.entity.CurrentWeatherEntity
import com.parenthq.app.weatherapp.domain.datasource.currentWeather.CurrentWeatherLocalDataSource
import com.parenthq.app.weatherapp.domain.datasource.currentWeather.CurrentWeatherRemoteDataSource
import com.parenthq.app.weatherapp.domain.model.CurrentWeatherResponse
import com.parenthq.app.weatherapp.utils.domain.RateLimiter
import com.parenthq.app.weatherapp.utils.domain.Resource
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-24
 */

class CurrentWeatherRepository @Inject constructor(
    private val currentWeatherRemoteDataSource: CurrentWeatherRemoteDataSource,
    private val currentWeatherLocalDataSource: CurrentWeatherLocalDataSource
) {

    private val currentWeatherRateLimit = RateLimiter<String>(30, TimeUnit.SECONDS)

    fun loadCurrentWeatherByGeoCords(lat: Double, lon: Double, fetchRequired: Boolean, units: String): LiveData<Resource<CurrentWeatherEntity>> {
        return object : NetworkBoundResource<CurrentWeatherEntity, CurrentWeatherResponse>() {
            override fun saveCallResult(item: CurrentWeatherResponse) = currentWeatherLocalDataSource.insertCurrentWeather(item)

            override fun shouldFetch(data: CurrentWeatherEntity?): Boolean = fetchRequired

            override fun loadFromDb(): LiveData<CurrentWeatherEntity> = currentWeatherLocalDataSource.getCurrentWeather()

            override fun createCall(): Single<CurrentWeatherResponse> = currentWeatherRemoteDataSource.getCurrentWeatherByGeoCords(lat, lon, units)

            override fun onFetchFailed() = currentWeatherRateLimit.reset(RATE_LIMITER_TYPE)
        }.asLiveData
    }
}
