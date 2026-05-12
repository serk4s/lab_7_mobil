package com.example.lab5

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lab5.ui.navigation.BookShelfApp
import com.example.lab5.ui.navigation.NotificationNavigationTarget
import com.example.lab5.ui.theme.Lab5Theme
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {
    private var notificationTarget by mutableStateOf<NotificationNavigationTarget?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationTarget = NotificationNavigationTarget.fromIntent(intent)
        enableEdgeToEdge()
        if (BuildConfig.MAPKIT_API_KEY.isNotBlank()) {
            MapKitFactory.initialize(this)
        }
        setContent {
            Lab5Theme {
                BookShelfApp(
                    notificationTarget = notificationTarget,
                    onNotificationTargetHandled = { notificationTarget = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationTarget = NotificationNavigationTarget.fromIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        if (BuildConfig.MAPKIT_API_KEY.isNotBlank()) {
            MapKitFactory.getInstance().onStart()
        }
    }

    override fun onStop() {
        if (BuildConfig.MAPKIT_API_KEY.isNotBlank()) {
            MapKitFactory.getInstance().onStop()
        }
        super.onStop()
    }
}
