package com.example.lab5.domain.repository

import com.example.lab5.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun observeBooks(): Flow<List<Book>>
    fun observeBook(bookId: String): Flow<Book?>
    suspend fun toggleFavorite(bookId: String)
}
