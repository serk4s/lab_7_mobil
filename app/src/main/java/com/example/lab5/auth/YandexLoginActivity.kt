package com.example.lab5.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk

class YandexLoginActivity : ComponentActivity() {
    private lateinit var launcher: ActivityResultLauncher<YandexAuthLoginOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sdk = YandexAuthSdk.create(YandexAuthOptions(this, true))
        launcher = registerForActivityResult(sdk.contract) { result ->
            lifecycleScope.launch {
                YandexAuthBridge.complete(result.toAuthResult())
                finish()
            }
        }
        launcher.launch(YandexAuthLoginOptions())
    }

    override fun onDestroy() {
        if (!isFinishing) {
            YandexAuthBridge.complete(AuthResult.Cancelled)
        }
        super.onDestroy()
    }

    private suspend fun YandexAuthResult.toAuthResult(): AuthResult {
        return when (this) {
            is YandexAuthResult.Success -> runCatching {
                AuthResult.Success(YandexProfileClient().loadUser(token.value))
            }.getOrElse {
                AuthResult.Success(
                    AuthUser(
                        token = token.value,
                        name = "Yandex ID",
                        provider = AuthProvider.YANDEX
                    )
                )
            }

            is YandexAuthResult.Failure -> AuthResult.Error(
                exception.message ?: "Не удалось войти через Яндекс ID"
            )

            YandexAuthResult.Cancelled -> AuthResult.Cancelled
        }
    }
}
