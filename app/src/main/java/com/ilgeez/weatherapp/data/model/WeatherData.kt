package com.ilgeez.weatherapp.data.model

data class WeatherData(
    val city: String,
    val currentTemp: Int,
    val condition: String,
    val highTemp: Int,
    val lowTemp: Int,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
)

data class HourlyForecast(
    val time: String,
    val temp: Int,
    val icon: String
)

data class DailyForecast(
    val dayName: String,
    val low: Int,
    val high: Int,
    val icon: String
)
