package com.parenthq.app.weatherapp.di.module

import com.parenthq.app.weatherapp.ui.dashboard.DashboardFragment
import com.parenthq.app.weatherapp.ui.search.SearchFragment
import com.parenthq.app.weatherapp.ui.splash.SplashFragment
import com.parenthq.app.weatherapp.ui.weather_detail.WeatherDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Furkan on 2019-10-26
 */

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeWeatherDetailFragment(): WeatherDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}
