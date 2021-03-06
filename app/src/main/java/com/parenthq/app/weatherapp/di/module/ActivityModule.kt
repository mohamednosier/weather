package com.parenthq.app.weatherapp.di.module

import com.parenthq.app.weatherapp.di.scope.PerActivity
import com.parenthq.app.weatherapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Furkan on 2019-10-16
 */

@Module
abstract class ActivityModule {

    /**
     * Injects [MainActivity]
     *
     * @return an instance of [MainActivity]
     */

    @PerActivity
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    internal abstract fun mainActivity(): MainActivity
}
