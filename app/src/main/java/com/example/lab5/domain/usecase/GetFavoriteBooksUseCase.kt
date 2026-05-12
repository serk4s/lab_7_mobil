package com.example.lab5.domain.usecase

import com.example.lab5.domain.model.Book
import com.example.lab5.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoriteBooksUseCase(
    private val repository: BookRepository
) {
    operator fun invoke(): Flow<List<Book>> {
        return repository.observeBooks().map { books ->
            books.filter { it.isFavorite }
        }
    }
}
