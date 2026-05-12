package com.example.lab5.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab5.config.RemoteConfigService
import com.example.lab5.profile.UserProfileService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileService: UserProfileService,
    remoteConfigService: RemoteConfigService
) : ViewModel() {
    val remoteConfigState = remoteConfigService.state

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private var profileJob: Job? = null
    private var lastSyncKey: String? = null

    fun syncAuthenticatedUser(name: String?, email: String?) {
        val syncKey = "${name.orEmpty()}|${email.orEmpty()}"
        if (lastSyncKey == syncKey && profileJob?.isActive == true) return
        lastSyncKey = syncKey
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, message = null) }
            runCatching {
                profileService.syncCurrentProfile(name = name, email = email)
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        message = "Не удалось синхронизировать профиль Firebase: ${error.message}"
                    )
                }
                return@launch
            }

            profileService.observeCurrentProfile()
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            message = "Ошибка подписки Firestore: ${error.message}"
                        )
                    }
                }
                .collect { profile ->
                    _state.update {
                        it.copy(
                            profile = profile,
                            isLoading = false,
                            message = null
                        )
                    }
                }
        }
    }
}
