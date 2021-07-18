package com.parenthq.app.weatherapp.ui.splash

import android.graphics.Color
import androidx.navigation.fragment.findNavController
import com.parenthq.app.weatherapp.R
import com.parenthq.app.weatherapp.core.BaseFragment
import com.parenthq.app.weatherapp.core.Constants
import com.parenthq.app.weatherapp.databinding.FragmentSplashBinding
import com.parenthq.app.weatherapp.di.Injectable
import com.parenthq.app.weatherapp.utils.extensions.hide
import com.parenthq.app.weatherapp.utils.extensions.show
import com.mikhaellopez.rxanimation.*
import io.reactivex.disposables.CompositeDisposable

class SplashFragment : BaseFragment<SplashFragmentViewModel, FragmentSplashBinding>(R.layout.fragment_splash, SplashFragmentViewModel::class.java), Injectable {

    var disposable = CompositeDisposable()

    override fun init() {
        super.init()

        if (binding.viewModel?.sharedPreferences?.getString(Constants.Coords.LON, "").isNullOrEmpty()) {
            binding.buttonExplore.show()
            binding.viewModel?.navigateDashboard = false
        } else {
            binding.buttonExplore.hide()
            binding.viewModel?.navigateDashboard = true
        }

        binding.viewModel?.navigateDashboard?.let { startSplashAnimation(it) }

        binding.buttonExplore.setOnClickListener {
            binding.viewModel?.navigateDashboard?.let { it1 -> endSplashAnimation(it1) }
        }

        binding.rootView.setOnClickListener {
            binding.viewModel?.navigateDashboard?.let { it1 -> endSplashAnimation(it1) }
        }
    }

    private fun startSplashAnimation(navigateToDashboard: Boolean) {
        disposable.add(
            RxAnimation.sequentially(



                binding.buttonExplore.fadeIn(1000L)
            ).doOnTerminate {
                findNavController().graph.startDestination = R.id.dashboardFragment // Little bit tricky solution :)
                if (navigateToDashboard)
                    endSplashAnimation(navigateToDashboard)
            }
                .subscribe()
        )
    }

    private fun endSplashAnimation(navigateToDashboard: Boolean) {
        disposable.add(
            RxAnimation.sequentially(



                binding.buttonExplore.fadeOut(300L),
                binding.rootView.backgroundColor(
                    Color.parseColor("#5D50FE"),
                    Color.parseColor("#FFFFFF"),
                    duration = 750L
                )
            )
                .doOnTerminate {
                    findNavController().graph.startDestination = R.id.dashboardFragment // Little bit tricky solution :)
                    if (navigateToDashboard)
                        navigate(R.id.action_splashFragment_to_dashboardFragment)
                    else
                        navigate(R.id.action_splashFragment_to_searchFragment)
                }
                .subscribe()

        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
