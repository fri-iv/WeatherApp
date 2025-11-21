package com.ilgeez.weatherapp.data.service

import com.ilgeez.weatherapp.data.model.WeatherApiResponse
import retrofit2.http.GET

interface ApiService {
    @GET("v1/forecast.json?key=fa8b3df74d4042b9aa7135114252304&q=55.7569,37.6151&days=3")
    suspend fun getWeather(): WeatherApiResponse
}
