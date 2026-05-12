package com.example.lab5.ui.auth

import com.example.lab5.auth.AuthProvider

data class LoginUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userName: String? = null,
    val provider: AuthProvider? = null,
    val login: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val message: String? = null
)
