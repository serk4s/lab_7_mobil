package com.example.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.core.common.AppSpacing

@Composable
fun EmptyStateCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(AppSpacing.large),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
