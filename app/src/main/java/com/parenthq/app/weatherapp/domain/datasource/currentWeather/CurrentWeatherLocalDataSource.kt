package com.parenthq.app.weatherapp.domain.datasource.currentWeather

import com.parenthq.app.weatherapp.db.dao.CurrentWeatherDao
import com.parenthq.app.weatherapp.db.entity.CurrentWeatherEntity
import com.parenthq.app.weatherapp.domain.model.CurrentWeatherResponse
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-24
 */

class CurrentWeatherLocalDataSource @Inject constructor(private val currentWeatherDao: CurrentWeatherDao) {

    fun getCurrentWeather() = currentWeatherDao.getCurrentWeather()

    fun insertCurrentWeather(currentWeather: CurrentWeatherResponse) = currentWeatherDao.deleteAndInsert(CurrentWeatherEntity(currentWeather))
}
