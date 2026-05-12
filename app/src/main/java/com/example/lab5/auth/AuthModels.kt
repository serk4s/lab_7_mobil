package com.example.lab5.auth

enum class AuthProvider(val analyticsName: String, val title: String) {
    YANDEX("yandex", "Яндекс ID"),
    VK("vk", "VK ID")
}

data class AuthUser(
    val token: String,
    val name: String,
    val provider: AuthProvider,
    val login: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null
)

sealed interface AuthResult {
    data class Success(val user: AuthUser) : AuthResult
    data class Error(val message: String) : AuthResult
    data object Cancelled : AuthResult
}
