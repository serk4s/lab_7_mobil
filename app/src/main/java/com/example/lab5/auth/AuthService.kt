package com.example.lab5.auth

import android.app.Activity

interface AuthService {
    suspend fun login(activity: Activity, provider: AuthProvider): AuthResult
    fun logout()
    fun getCurrentUser(): AuthUser?
}
