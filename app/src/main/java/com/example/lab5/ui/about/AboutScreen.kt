package com.example.lab5.ui.about

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.core.common.AppSpacing
import com.example.lab5.BuildConfig
import com.example.lab5.auth.AuthProvider
import com.example.lab5.config.RemoteConfigState
import com.example.lab5.profile.UserProfile
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AboutScreen(
    userName: String?,
    provider: AuthProvider?,
    login: String?,
    firstName: String?,
    lastName: String?,
    email: String?,
    firebaseProfile: UserProfile?,
    remoteConfigState: RemoteConfigState,
    profileMessage: String?,
    isProfileLoading: Boolean,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var routePoints by remember { mutableStateOf<List<Point>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            routePoints = buildRouteFromCurrentLocation(context)
            message = routeMessage(routePoints)
        } else {
            message = "Без разрешения геолокации маршрут от текущего положения недоступен."
        }
    }

    Column(
        modifier = modifier.padding(AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        ProfileCard(
            userName = userName,
            provider = provider,
            login = login,
            firstName = firstName,
            lastName = lastName,
            email = email,
            firebaseProfile = firebaseProfile,
            remoteConfigState = remoteConfigState,
            profileMessage = profileMessage,
            isProfileLoading = isProfileLoading,
            onLogoutClick = onLogoutClick
        )

        Text(text = "О нас", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "BookSpace — учебная компания, которая помогает читателям собирать личные подборки книг, отслеживать избранное и быстрее находить полезную литературу.",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Офис: Новосибирск, Красный проспект, 17",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (BuildConfig.MAPKIT_API_KEY.isBlank()) {
            Text(
                text = "Для отображения карты добавьте MAPKIT_API_KEY в local.properties.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            OfficeMap(
                routePoints = routePoints,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            )
        }

        Button(
            onClick = {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    routePoints = buildRouteFromCurrentLocation(context)
                    message = routeMessage(routePoints)
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Outlined.Route, contentDescription = null)
            Text(text = "Построить маршрут", modifier = Modifier.padding(start = AppSpacing.small))
        }

        message?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileCard(
    userName: String?,
    provider: AuthProvider?,
    login: String?,
    firstName: String?,
    lastName: String?,
    email: String?,
    firebaseProfile: UserProfile?,
    remoteConfigState: RemoteConfigState,
    profileMessage: String?,
    isProfileLoading: Boolean,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            Text(text = "Профиль", style = MaterialTheme.typography.titleMedium)
            Text(
                text = userName?.takeIf { it.isNotBlank() } ?: "Пользователь",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Вход через: ${provider?.title ?: "неизвестный провайдер"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (remoteConfigState.welcomeBannerText.isNotBlank()) {
                Text(
                    text = remoteConfigState.welcomeBannerText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            ProfileLine(label = "Логин", value = login)
            ProfileLine(label = "Имя", value = firstName)
            ProfileLine(label = "Фамилия", value = lastName)
            ProfileLine(label = "Почта", value = email)
            ProfileLine(label = "Firebase userId", value = firebaseProfile?.userId)
            ProfileLine(label = "Firestore имя", value = firebaseProfile?.name)
            ProfileLine(label = "Firestore email", value = firebaseProfile?.email)
            ProfileLine(label = "FCM-токен", value = firebaseProfile?.fcmToken?.shortToken())
            ProfileLine(label = "Обновлено", value = firebaseProfile?.updatedAt?.formatProfileDate())
            if (remoteConfigState.experimentalFeatureEnabled) {
                Text(
                    text = "Экспериментальная функция активна.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (isProfileLoading) {
                Text(
                    text = "Синхронизируем профиль Firebase...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            profileMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Outlined.Logout, contentDescription = null)
                Text(text = "Выйти", modifier = Modifier.padding(start = AppSpacing.small))
            }
        }
    }
}

private fun String.shortToken(): String {
    return if (length <= 24) this else "${take(12)}...${takeLast(8)}"
}

private fun java.util.Date.formatProfileDate(): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(this)
}

@Composable
private fun ProfileLine(label: String, value: String?) {
    Text(
        text = "$label: ${value?.takeIf { it.isNotBlank() } ?: "нет данных"}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun OfficeMap(
    routePoints: List<Point>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(mapView) {
        mapView.onStart()
        onDispose { mapView.onStop() }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView },
        update = { drawOfficeMap(it, routePoints) }
    )
}

private fun drawOfficeMap(mapView: MapView, routePoints: List<Point>) {
    val map = mapView.mapWindow.map
    map.move(CameraPosition(OFFICE_POINT, 14f, 0f, 0f))
    map.mapObjects.clear()
    map.mapObjects.addPlacemark(OFFICE_POINT)
    if (routePoints.size >= 2) {
        map.mapObjects.addPolyline(Polyline(routePoints)).strokeWidth = 4f
    }
}

private fun buildRouteFromCurrentLocation(context: Context): List<Point> {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val location = runCatching {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }.getOrNull() ?: return emptyList()
    return listOf(Point(location.latitude, location.longitude), OFFICE_POINT)
}

private fun routeMessage(routePoints: List<Point>): String {
    return if (routePoints.isEmpty()) {
        "Не удалось получить текущую геопозицию. Проверьте настройки геолокации."
    } else {
        "Маршрут от текущего положения до офиса построен."
    }
}

private val OFFICE_POINT = Point(55.030204, 82.920430)
