package com.example.lab5

import android.app.Application
import android.util.Log
import com.example.lab5.analytics.AppMetricaAnalyticsService
import com.example.lab5.messaging.FcmTokenStorage
import com.example.lab5.profile.FirebaseUserProfileService
import com.google.firebase.messaging.FirebaseMessaging
import com.yandex.mapkit.MapKitFactory

class Lab5Application : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.initialize(this)
        initMapKit()
        initFcmToken()
        AppMetricaAnalyticsService.activate(this, BuildConfig.APPMETRICA_API_KEY)
    }

    private fun initMapKit() {
        if (BuildConfig.MAPKIT_API_KEY.isBlank()) {
            Log.w("Lab5Application", "MAPKIT_API_KEY is empty. Map screen will show setup message.")
            return
        }
        runCatching {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        }.onFailure { error ->
            Log.w("Lab5Application", "MapKit api key was already set or failed to initialize.", error)
        }
    }

    private fun initFcmToken() {
        if (!BuildConfig.HAS_GOOGLE_SERVICES) return
        val tokenStorage = FcmTokenStorage(this)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Failed to get FCM token", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result.orEmpty()
                tokenStorage.save(token)
                Log.d(TAG, "FCM token: $token")
                FirebaseUserProfileService(tokenStorage).updateTokenIfUserExists(token)
            }
    }

    private companion object {
        const val TAG = "Lab5Application"
    }
}
