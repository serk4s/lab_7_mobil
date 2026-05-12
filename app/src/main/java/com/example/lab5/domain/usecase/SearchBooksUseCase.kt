package com.example.lab5.domain.usecase

import com.example.lab5.domain.model.Book

class SearchBooksUseCase {
    operator fun invoke(books: List<Book>, query: String): List<Book> {
        if (query.isBlank()) return books
        val normalizedQuery = query.trim().lowercase()
        return books.filter { book ->
            book.title.lowercase().contains(normalizedQuery) ||
                book.author.lowercase().contains(normalizedQuery) ||
                book.genre.lowercase().contains(normalizedQuery)
        }
    }
}
