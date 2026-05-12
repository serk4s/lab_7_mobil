package com.example.lab5.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab5.analytics.AnalyticsService
import com.example.lab5.auth.AuthProvider
import com.example.lab5.auth.AuthResult
import com.example.lab5.auth.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: AuthService,
    private val analytics: AnalyticsService
) : ViewModel() {
    private val currentUser = authService.getCurrentUser()
    private val _state = MutableStateFlow(
        LoginUiState(
            isAuthenticated = currentUser != null,
            userName = currentUser?.name,
            provider = currentUser?.provider,
            login = currentUser?.login,
            firstName = currentUser?.firstName,
            lastName = currentUser?.lastName,
            email = currentUser?.email
        )
    )
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun login(activity: Activity, provider: AuthProvider) {
        _state.update { it.copy(isLoading = true, message = null) }
        viewModelScope.launch {
            when (val result = authService.login(activity, provider)) {
                is AuthResult.Success -> {
                    analytics.trackEvent(
                        name = "user_logged_in",
                        params = mapOf("provider" to provider.analyticsName)
                    )
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            userName = result.user.name,
                            provider = result.user.provider,
                            login = result.user.login,
                            firstName = result.user.firstName,
                            lastName = result.user.lastName,
                            email = result.user.email,
                            message = null
                        )
                    }
                }

                is AuthResult.Error -> {
                    analytics.trackError("auth_failed_${provider.analyticsName}", null)
                    _state.update { it.copy(isLoading = false, message = result.message) }
                }

                AuthResult.Cancelled -> {
                    _state.update { it.copy(isLoading = false, message = "Вход отменён") }
                }
            }
        }
    }

    fun logout() {
        authService.logout()
        _state.update { LoginUiState() }
    }
}
