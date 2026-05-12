package com.example.lab5.config

import kotlinx.coroutines.flow.StateFlow

interface RemoteConfigService {
    val state: StateFlow<RemoteConfigState>
    fun initialize()
}
