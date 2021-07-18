package com.parenthq.app.weatherapp.ui.dashboard.forecast

import androidx.databinding.ObservableField
import com.parenthq.app.weatherapp.core.BaseViewModel
import com.parenthq.app.weatherapp.domain.model.ListItem
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-25
 */

class ForecastItemViewModel @Inject internal constructor() : BaseViewModel() {
    var item = ObservableField<ListItem>()
}
