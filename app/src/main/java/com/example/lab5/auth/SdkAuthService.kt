package com.example.lab5.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.lab5.BuildConfig

class SdkAuthService(
    private val storage: AuthStorage
) : AuthService {
    override suspend fun login(activity: Activity, provider: AuthProvider): AuthResult {
        val missingConfig = when (provider) {
            AuthProvider.YANDEX -> BuildConfig.YANDEX_CLIENT_ID.isBlank()
            AuthProvider.VK -> BuildConfig.VK_CLIENT_ID.isBlank()
        }
        if (missingConfig) {
            return AuthResult.Error(
                "Для входа через ${provider.title} нужно заполнить ключи в local.properties"
            )
        }

        return when (provider) {
            AuthProvider.YANDEX -> loginWithYandex(activity)
            AuthProvider.VK -> AuthResult.Error("VK ID пока отключён: для проверки лабораторной используем Яндекс ID")
        }
    }

    private suspend fun loginWithYandex(activity: Activity): AuthResult {
        Log.d("SdkAuthService", "Starting Yandex ID auth from ${activity.localClassName}")
        val deferred = YandexAuthBridge.prepare()
        activity.startActivity(Intent(activity, YandexLoginActivity::class.java))
        return when (val result = deferred.await()) {
            is AuthResult.Success -> {
                storage.save(result.user)
                result
            }

            else -> result
        }
    }

    override fun logout() {
        storage.clear()
    }

    override fun getCurrentUser(): AuthUser? = storage.get()
}
