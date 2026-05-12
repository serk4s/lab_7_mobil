package com.example.lab5.ui.books

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.core.common.AppSpacing
import com.example.core.common.UiState
import com.example.core.ui.EmptyStateCard
import com.example.lab5.domain.model.Book
import java.util.Locale

@Composable
fun BooksScreen(
    state: BookListState,
    welcomeBannerText: String,
    experimentalFeatureEnabled: Boolean,
    onQueryChange: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Поиск по каталогу") },
            placeholder = { Text("Название, автор или жанр") }
        )

        if (welcomeBannerText.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(AppSpacing.medium),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall)
                ) {
                    Text(
                        text = welcomeBannerText,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (experimentalFeatureEnabled) {
                        Text(
                            text = "Экспериментальная подборка включена через Remote Config.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        when (val booksState = state.booksState) {
            UiState.Loading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Empty -> {
                EmptyStateCard(
                    title = booksState.title,
                    subtitle = booksState.subtitle
                )
            }

            is UiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
                    items(items = booksState.value, key = { it.id }) { book ->
                        BookCard(
                            book = book,
                            onClick = { onBookClick(book.id) },
                            onFavoriteToggle = { onFavoriteToggle(book.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookCard(
    book: Book,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall)
                ) {
                    Text(text = book.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Переключить избранное",
                        tint = if (book.isFavorite) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
                AssistChip(onClick = onClick, label = { Text(book.genre) })
                AssistChip(
                    onClick = onClick,
                    label = { Text(book.readingStatus.name.lowercase(Locale.getDefault())) }
                )
            }

            Text(
                text = book.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
