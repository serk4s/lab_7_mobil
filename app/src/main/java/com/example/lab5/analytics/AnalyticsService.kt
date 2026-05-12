package com.example.lab5.analytics

interface AnalyticsService {
    fun trackEvent(name: String, params: Map<String, Any> = emptyMap())
    fun trackError(message: String, error: Throwable? = null)
}
