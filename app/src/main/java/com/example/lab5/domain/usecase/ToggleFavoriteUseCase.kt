package com.example.lab5.domain.usecase

import com.example.lab5.domain.repository.BookRepository

class ToggleFavoriteUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: String) {
        repository.toggleFavorite(bookId)
    }
}
