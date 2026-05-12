package com.example.lab5.ui.books

import com.example.core.common.UiState
import com.example.lab5.domain.model.Book

data class BookListState(
    val query: String = "",
    val booksState: UiState<List<Book>> = UiState.Loading
)

data class BookDetailsState(
    val bookState: UiState<Book> = UiState.Loading
)
