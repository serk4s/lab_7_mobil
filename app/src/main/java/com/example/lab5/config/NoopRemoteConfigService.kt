package com.example.lab5.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoopRemoteConfigService : RemoteConfigService {
    private val _state = MutableStateFlow(
        RemoteConfigState(
            welcomeBannerText = "Добавьте app/google-services.json для включения Firebase",
            experimentalFeatureEnabled = false,
            isLoaded = false
        )
    )
    override val state: StateFlow<RemoteConfigState> = _state.asStateFlow()

    override fun initialize() = Unit
}
