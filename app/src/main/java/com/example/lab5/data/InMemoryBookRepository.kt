package com.example.lab5.data

import com.example.lab5.domain.model.Book
import com.example.lab5.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryBookRepository(
    initialBooks: List<Book> = BookSeed.books
) : BookRepository {
    private val books = MutableStateFlow(initialBooks)

    override fun observeBooks(): Flow<List<Book>> = books

    override fun observeBook(bookId: String): Flow<Book?> {
        return books.map { items -> items.firstOrNull { it.id == bookId } }
    }

    override suspend fun toggleFavorite(bookId: String) {
        books.update { items ->
            items.map { book ->
                if (book.id == bookId) {
                    book.copy(isFavorite = !book.isFavorite)
                } else {
                    book
                }
            }
        }
    }
}
