package com.example.lab5.ui.auth

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.core.common.AppSpacing
import com.example.lab5.auth.AuthProvider

@Composable
fun LoginScreen(
    state: LoginUiState,
    onLoginClick: (Activity, AuthProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = LocalContext.current as Activity

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Книжная полка",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Войдите через внешний сервис, чтобы продолжить",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = AppSpacing.small, bottom = AppSpacing.large)
        )

        Button(
            onClick = { onLoginClick(activity, AuthProvider.YANDEX) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Outlined.Login, contentDescription = null)
            Text(text = "Войти через Яндекс ID", modifier = Modifier.padding(start = AppSpacing.small))
        }

        OutlinedButton(
            onClick = { onLoginClick(activity, AuthProvider.VK) },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.small)
        ) {
            Icon(imageVector = Icons.Outlined.Login, contentDescription = null)
            Text(text = "Войти через VK ID", modifier = Modifier.padding(start = AppSpacing.small))
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = AppSpacing.medium))
        }
        state.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = AppSpacing.medium)
            )
        }
    }
}
