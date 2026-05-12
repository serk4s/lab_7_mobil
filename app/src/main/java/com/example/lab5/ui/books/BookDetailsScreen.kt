package com.example.lab5.ui.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.core.common.AppSpacing
import com.example.core.common.UiState
import com.example.core.ui.EmptyStateCard
import com.example.lab5.domain.model.Book

@Composable
fun BookDetailsScreen(
    state: BookDetailsState,
    onBackClick: () -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (val bookState = state.bookState) {
        UiState.Loading -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Empty -> {
            EmptyStateCard(
                title = bookState.title,
                subtitle = bookState.subtitle,
                modifier = modifier.padding(AppSpacing.medium)
            )
        }

        is UiState.Success -> {
            BookDetailsContent(
                book = bookState.value,
                onBackClick = onBackClick,
                onFavoriteToggle = onFavoriteToggle,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun BookDetailsContent(
    book: Book,
    onBackClick: () -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text(
                text = "Детали книги",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onFavoriteToggle(book.id) }) {
                Icon(
                    imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Переключить избранное"
                )
            }
        }

        Text(text = book.title, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "${book.author} • ${book.year}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
            AssistChip(onClick = {}, label = { Text(book.genre) })
            AssistChip(onClick = {}, label = { Text("Рейтинг ${book.rating}") })
        }

        Text(text = book.description, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "Статус чтения: ${book.readingStatus}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
