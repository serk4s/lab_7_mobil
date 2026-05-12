package com.example.lab5.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedAuthStorage(context: Context) : AuthStorage {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun save(user: AuthUser) {
        preferences.edit()
            .putString(KEY_TOKEN, user.token)
            .putString(KEY_NAME, user.name)
            .putString(KEY_PROVIDER, user.provider.name)
            .putString(KEY_LOGIN, user.login)
            .putString(KEY_FIRST_NAME, user.firstName)
            .putString(KEY_LAST_NAME, user.lastName)
            .putString(KEY_EMAIL, user.email)
            .apply()
    }

    override fun get(): AuthUser? {
        val token = preferences.getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() } ?: return null
        val provider = preferences.getString(KEY_PROVIDER, null)
            ?.let { runCatching { AuthProvider.valueOf(it) }.getOrNull() }
            ?: return null
        return AuthUser(
            token = token,
            name = preferences.getString(KEY_NAME, null).orEmpty().ifBlank { provider.title },
            provider = provider,
            login = preferences.getString(KEY_LOGIN, null),
            firstName = preferences.getString(KEY_FIRST_NAME, null),
            lastName = preferences.getString(KEY_LAST_NAME, null),
            email = preferences.getString(KEY_EMAIL, null)
        )
    }

    override fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val FILE_NAME = "secure_auth"
        const val KEY_TOKEN = "token"
        const val KEY_NAME = "name"
        const val KEY_PROVIDER = "provider"
        const val KEY_LOGIN = "login"
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_EMAIL = "email"
    }
}
