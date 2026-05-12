package com.example.lab5.auth

import android.app.Activity

class FakeAuthService(
    private val result: AuthResult,
    private val storage: AuthStorage = InMemoryAuthStorage()
) : AuthService {
    override suspend fun login(activity: Activity, provider: AuthProvider): AuthResult {
        if (result is AuthResult.Success) {
            storage.save(result.user)
        }
        return result
    }

    override fun logout() {
        storage.clear()
    }

    override fun getCurrentUser(): AuthUser? = storage.get()
}
