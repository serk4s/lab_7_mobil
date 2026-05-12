package com.example.lab5.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class YandexProfileClient(
    private val client: OkHttpClient = OkHttpClient()
) {
    suspend fun loadUser(token: String): AuthUser = withContext(Dispatchers.IO) {
        val url = "https://login.yandex.ru/info".toHttpUrl().newBuilder()
            .addQueryParameter("format", "json")
            .addQueryParameter("oauth_token", token)
            .build()
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("Yandex profile request failed: HTTP ${response.code}")
            }
            val body = response.body?.string().orEmpty()
            val json = JSONObject(body)
            val firstName = json.optStringOrNull("first_name")
            val lastName = json.optStringOrNull("last_name")
            val login = json.optStringOrNull("login")
            val email = json.optStringOrNull("default_email")
                ?: json.optJSONArray("emails")?.optString(0)?.takeIf { it.isNotBlank() }
            val displayName = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .ifBlank { json.optStringOrNull("display_name") ?: login ?: "Yandex ID" }

            AuthUser(
                token = token,
                name = displayName,
                provider = AuthProvider.YANDEX,
                login = login,
                firstName = firstName,
                lastName = lastName,
                email = email
            )
        }
    }

    private fun JSONObject.optStringOrNull(name: String): String? {
        return optString(name).takeIf { it.isNotBlank() }
    }
}
