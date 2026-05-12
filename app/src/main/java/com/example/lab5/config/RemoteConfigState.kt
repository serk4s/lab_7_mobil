package com.example.lab5.config

data class RemoteConfigState(
    val welcomeBannerText: String = "Добро пожаловать в BookSpace",
    val experimentalFeatureEnabled: Boolean = false,
    val isLoaded: Boolean = false
)
