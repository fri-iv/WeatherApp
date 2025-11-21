package com.ilgeez.weatherapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ilgeez.weatherapp.R
import com.ilgeez.weatherapp.data.model.DailyForecast
import com.ilgeez.weatherapp.data.model.HourlyForecast
import com.ilgeez.weatherapp.data.model.WeatherData
import com.ilgeez.weatherapp.ui.theme.CardBorder
import com.ilgeez.weatherapp.ui.theme.WeatherBlueBottom
import com.ilgeez.weatherapp.ui.theme.WeatherBlueTop
import com.ilgeez.weatherapp.ui.theme.White
import com.ilgeez.weatherapp.ui.theme.White20
import com.ilgeez.weatherapp.ui.theme.White50
import com.ilgeez.weatherapp.ui.theme.White80

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val state by viewModel.getState().collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(WeatherBlueTop, WeatherBlueBottom)
                )
            )
    ) {
        when (val currentState = state) {
            is WeatherState.OnProgress -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = White
                )
            }

            is WeatherState.OnError -> {
                ErrorDialog(
                    message = currentState.message,
                    onRetry = { viewModel.loadWeather() }
                )
            }

            is WeatherState.OnSuccess -> {
                WeatherContent(currentState.data)
            }
        }
    }
}

@Composable
fun ErrorDialog(message: String, onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(R.string.error_title)) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        },
        icon = { Icon(Icons.Default.Warning, contentDescription = null) }
    )
}

@Composable
fun WeatherContent(data: WeatherData) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = data.city,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "${data.currentTemp}°",
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            text = data.condition,
            style = MaterialTheme.typography.titleMedium,
            color = White80
        )

        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = "${stringResource(R.string.max)} ${data.highTemp}°  ${stringResource(R.string.min)} ${data.lowTemp}°",
            style = MaterialTheme.typography.bodyMedium,
            color = White
        )

        Spacer(modifier = Modifier.height(48.dp))

        WeatherCard {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = stringResource(R.string.hourly),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    color = White20,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(data.hourly) { hourly ->
                        HourlyItem(hourly)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        WeatherCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.dayly),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                data.daily.forEachIndexed { index, day ->
                    DailyItemRow(day)
                    if (index < data.daily.lastIndex) {
                        HorizontalDivider(color = White20)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun WeatherCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, CardBorder),
        content = content
    )
}

@Composable
fun HourlyItem(item: HourlyForecast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = item.time,
            style = MaterialTheme.typography.bodyMedium
        )

        AsyncImage(
            model = item.icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "${item.temp}°",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun DailyItemRow(item: DailyForecast) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.dayName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.width(60.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        AsyncImage(
            model = item.icon,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.width(100.dp), horizontalArrangement = Arrangement.End) {
            Text(
                text = "${item.low}°",
                style = MaterialTheme.typography.bodyMedium,
                color = White50
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${item.high}°",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
