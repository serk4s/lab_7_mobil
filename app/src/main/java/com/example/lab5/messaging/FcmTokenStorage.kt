package com.example.lab5.messaging

import android.content.Context

class FcmTokenStorage(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun save(token: String) {
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun get(): String = preferences.getString(KEY_TOKEN, null).orEmpty()

    private companion object {
        const val FILE_NAME = "fcm_token_storage"
        const val KEY_TOKEN = "fcm_token"
    }
}
