package com.example.lab5.ui.profile

import com.example.lab5.profile.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = false,
    val message: String? = null
)
