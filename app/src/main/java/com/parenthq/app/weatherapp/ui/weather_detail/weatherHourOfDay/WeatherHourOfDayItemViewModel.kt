package com.parenthq.app.weatherapp.ui.weather_detail.weatherHourOfDay

import androidx.databinding.ObservableField
import com.parenthq.app.weatherapp.core.BaseViewModel
import com.parenthq.app.weatherapp.domain.model.ListItem
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-26
 */

class WeatherHourOfDayItemViewModel @Inject internal constructor() : BaseViewModel() {
    var item = ObservableField<ListItem>()
}
