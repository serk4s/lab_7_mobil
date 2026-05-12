package com.example.lab5.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NoopUserProfileService : UserProfileService {
    override suspend fun syncCurrentProfile(name: String?, email: String?) = Unit
    override fun observeCurrentProfile(): Flow<UserProfile?> = flowOf(null)
    override fun updateTokenIfUserExists(token: String) = Unit
}
