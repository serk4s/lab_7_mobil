package com.example.lab5.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.UiState
import com.example.lab5.analytics.AnalyticsService
import com.example.lab5.domain.model.Book
import com.example.lab5.domain.usecase.GetBookDetailsUseCase
import com.example.lab5.domain.usecase.GetBooksUseCase
import com.example.lab5.domain.usecase.GetFavoriteBooksUseCase
import com.example.lab5.domain.usecase.SearchBooksUseCase
import com.example.lab5.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BooksViewModel(
    private val getBooks: GetBooksUseCase,
    private val getBookDetails: GetBookDetailsUseCase,
    private val getFavorites: GetFavoriteBooksUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val searchBooks: SearchBooksUseCase,
    private val analytics: AnalyticsService
) : ViewModel() {
    private val query = MutableStateFlow("")

    val catalogState: StateFlow<BookListState> = combine(
        getBooks(),
        query
    ) { books, currentQuery ->
        val filteredBooks = searchBooks(books, currentQuery)
        BookListState(
            query = currentQuery,
            booksState = toListState(
                items = filteredBooks,
                emptyTitle = "Книги не найдены",
                emptySubtitle = "Попробуй изменить запрос или очистить поиск."
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BookListState()
    )

    val favoritesState: StateFlow<BookListState> = combine(
        getFavorites(),
        query
    ) { books, currentQuery ->
        val filteredBooks = searchBooks(books, currentQuery)
        BookListState(
            query = currentQuery,
            booksState = toListState(
                items = filteredBooks,
                emptyTitle = "Пока нет избранного",
                emptySubtitle = "Добавь книги в избранное из каталога, чтобы они появились здесь."
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BookListState()
    )

    fun detailsState(bookId: String): StateFlow<BookDetailsState> {
        return getBookDetails(bookId)
            .map { book ->
                BookDetailsState(
                    bookState = if (book == null) {
                        UiState.Empty(
                            title = "Книга не найдена",
                            subtitle = "Похоже, этой книги уже нет в локальном каталоге."
                        )
                    } else {
                        UiState.Success(book)
                    }
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = BookDetailsState()
            )
    }

    fun onQueryChange(value: String) {
        query.update { value }
        analytics.trackEvent("books_search_changed", mapOf("query" to value))
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch {
            toggleFavorite.invoke(bookId)
            analytics.trackEvent("book_favorite_toggled", mapOf("book_id" to bookId))
        }
    }

    fun trackScreenViewed(screenName: String) {
        analytics.trackEvent("screen_viewed", mapOf("screen_name" to screenName))
    }

    private fun toListState(
        items: List<Book>,
        emptyTitle: String,
        emptySubtitle: String
    ): UiState<List<Book>> {
        return if (items.isEmpty()) {
            UiState.Empty(
                title = emptyTitle,
                subtitle = emptySubtitle
            )
        } else {
            UiState.Success(items)
        }
    }
}
