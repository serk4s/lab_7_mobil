package com.example.lab5.profile

import kotlinx.coroutines.flow.Flow

interface UserProfileService {
    suspend fun syncCurrentProfile(name: String?, email: String?)
    fun observeCurrentProfile(): Flow<UserProfile?>
    fun updateTokenIfUserExists(token: String)
}
