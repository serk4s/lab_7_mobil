package com.example.lab5.analytics

import android.content.Context
import android.util.Log
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

class AppMetricaAnalyticsService : AnalyticsService {
    override fun trackEvent(name: String, params: Map<String, Any>) {
        if (params.isEmpty()) {
            AppMetrica.reportEvent(name)
        } else {
            AppMetrica.reportEvent(name, params.mapValues { it.value.toString() })
        }
    }

    override fun trackError(message: String, error: Throwable?) {
        AppMetrica.reportError(message, error)
    }

    companion object {
        fun activate(context: Context, apiKey: String) {
            if (apiKey.isBlank()) {
                Log.w("AppMetrica", "APPMETRICA_API_KEY is empty. Analytics SDK is not activated.")
                return
            }
            val config = AppMetricaConfig.newConfigBuilder(apiKey).build()
            AppMetrica.activate(context.applicationContext, config)
            if (context is android.app.Application) {
                AppMetrica.enableActivityAutoTracking(context)
            }
        }
    }
}
