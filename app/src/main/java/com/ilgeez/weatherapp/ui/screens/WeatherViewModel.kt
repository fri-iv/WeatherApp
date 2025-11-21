package com.ilgeez.weatherapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilgeez.weatherapp.data.model.WeatherData
import com.ilgeez.weatherapp.data.model.toData
import com.ilgeez.weatherapp.data.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WeatherState {
    object OnProgress : WeatherState
    data class OnSuccess(val data: WeatherData) : WeatherState
    data class OnError(val message: String) : WeatherState
}

@HiltViewModel
class WeatherViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val state = MutableStateFlow<WeatherState>(WeatherState.OnProgress)
    fun getState() = state

    init {
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {
            state.value = WeatherState.OnProgress
            try {
                val response = apiService.getWeather()
                val weatherData = response.toData()
                state.value = WeatherState.OnSuccess(weatherData)
            } catch (e: Exception) {
                e.printStackTrace()
                state.value = WeatherState.OnError("${e.localizedMessage}")
            }
        }
    }
}
