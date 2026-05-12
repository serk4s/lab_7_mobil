package com.example.lab5.profile

import java.util.Date

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val fcmToken: String = "",
    val updatedAt: Date? = null
)
