package com.ilgeez.weatherapp.data.model

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

data class WeatherApiResponse(
    @SerializedName("location") val location: Location,
    @SerializedName("current") val current: Current,
    @SerializedName("forecast") val forecast: Forecast
)

data class Location(
    @SerializedName("name") val name: String
)

data class Current(
    @SerializedName("temp_c") val tempC: Float,
    @SerializedName("condition") val condition: Condition
)

data class Condition(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val iconUrl: String
)

data class Forecast(
    @SerializedName("forecastday") val forecastDay: List<ForecastDay>
)

data class ForecastDay(
    @SerializedName("date_epoch") val dateEpoch: Long,
    @SerializedName("day") val day: Day,
    @SerializedName("hour") val hour: List<Hour>
)

data class Day(
    @SerializedName("maxtemp_c") val maxTempC: Float,
    @SerializedName("mintemp_c") val minTempC: Float,
    @SerializedName("condition") val condition: Condition
)

data class Hour(
    @SerializedName("time_epoch") val timeEpoch: Long,
    @SerializedName("temp_c") val tempC: Float,
    @SerializedName("condition") val condition: Condition
)

fun WeatherApiResponse.toData(): WeatherData {
    val hourFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
    val dayFormatter =
        DateTimeFormatter.ofPattern("EEE", Locale("ru")).withZone(ZoneId.systemDefault())

    val hourlyList = this.forecast.forecastDay.firstOrNull()?.hour?.map { hour ->
        HourlyForecast(
            time = hourFormatter.format(Instant.ofEpochSecond(hour.timeEpoch)),
            temp = hour.tempC.roundToInt(),
            icon = "https:" + hour.condition.iconUrl
        )
    } ?: emptyList()

    val dailyList = this.forecast.forecastDay.mapIndexed { index, day ->
        DailyForecast(
            dayName = dayFormatter.format(
                Instant.ofEpochSecond(
                    day.dateEpoch
                )
            ).replaceFirstChar { it.uppercase() },
            low = day.day.minTempC.roundToInt(),
            high = day.day.maxTempC.roundToInt(),
            icon = "https:" + day.day.condition.iconUrl
        )
    }

    return WeatherData(
        city = this.location.name,
        currentTemp = this.current.tempC.roundToInt(),
        condition = this.current.condition.text,
        highTemp = this.forecast.forecastDay.first().day.maxTempC.roundToInt(),
        lowTemp = this.forecast.forecastDay.first().day.minTempC.roundToInt(),
        hourly = hourlyList,
        daily = dailyList
    )
}
