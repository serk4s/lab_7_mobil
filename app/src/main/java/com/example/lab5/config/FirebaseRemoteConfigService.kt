package com.example.lab5.config

import android.util.Log
import com.example.lab5.R
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseRemoteConfigService(
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
) : RemoteConfigService {
    private val _state = MutableStateFlow(RemoteConfigState())
    override val state: StateFlow<RemoteConfigState> = _state.asStateFlow()

    private var initialized = false

    override fun initialize() {
        if (initialized) return
        initialized = true

        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            .addOnCompleteListener {
                applyState(isLoaded = false)
                remoteConfig.fetchAndActivate()
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "Remote Config fetch failed", task.exception)
                        }
                        applyState(isLoaded = task.isSuccessful)
                    }
            }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    Log.d(TAG, "Remote Config updated keys: ${configUpdate.updatedKeys}")
                    applyState(isLoaded = true)
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Remote Config realtime update failed: ${error.code}", error)
            }
        })
    }

    private fun applyState(isLoaded: Boolean) {
        _state.value = RemoteConfigState(
            welcomeBannerText = remoteConfig.getString(KEY_WELCOME_BANNER),
            experimentalFeatureEnabled = remoteConfig.getBoolean(KEY_EXPERIMENTAL_FEATURE),
            isLoaded = isLoaded
        )
    }

    private companion object {
        const val TAG = "RemoteConfigService"
        const val KEY_WELCOME_BANNER = "welcome_banner_text"
        const val KEY_EXPERIMENTAL_FEATURE = "experimental_feature_enabled"
    }
}
