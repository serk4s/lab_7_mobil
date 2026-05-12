package com.example.lab5.domain.usecase

import com.example.lab5.domain.model.Book
import com.example.lab5.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class GetBooksUseCase(
    private val repository: BookRepository
) {
    operator fun invoke(): Flow<List<Book>> = repository.observeBooks()
}
