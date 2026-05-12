package com.example.lab5.analytics

class FakeAnalyticsService : AnalyticsService {
    data class Event(val name: String, val params: Map<String, Any>)

    val events = mutableListOf<Event>()
    val errors = mutableListOf<Pair<String, Throwable?>>()

    override fun trackEvent(name: String, params: Map<String, Any>) {
        events += Event(name, params)
    }

    override fun trackError(message: String, error: Throwable?) {
        errors += message to error
    }
}
