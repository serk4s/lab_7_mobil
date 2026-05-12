package com.example.lab5

import android.app.Activity
import com.example.lab5.analytics.FakeAnalyticsService
import com.example.lab5.auth.AuthProvider
import com.example.lab5.auth.AuthResult
import com.example.lab5.auth.AuthUser
import com.example.lab5.auth.FakeAuthService
import com.example.lab5.data.BookSeed
import com.example.lab5.data.InMemoryBookRepository
import com.example.lab5.domain.usecase.GetBookDetailsUseCase
import com.example.lab5.domain.usecase.GetBooksUseCase
import com.example.lab5.domain.usecase.GetFavoriteBooksUseCase
import com.example.lab5.domain.usecase.SearchBooksUseCase
import com.example.lab5.domain.usecase.ToggleFavoriteUseCase
import com.example.lab5.ui.auth.LoginViewModel
import com.example.lab5.ui.books.BooksViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelAnalyticsTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun booksViewModel_tracksScreenViewed() {
        val analytics = FakeAnalyticsService()
        val viewModel = booksViewModel(analytics)

        viewModel.trackScreenViewed("catalog")

        assertEquals("screen_viewed", analytics.events.single().name)
        assertEquals("catalog", analytics.events.single().params["screen_name"])
    }

    @Test
    fun booksViewModel_tracksFavoriteToggle() = runTest {
        val analytics = FakeAnalyticsService()
        val viewModel = booksViewModel(analytics)

        viewModel.toggleFavorite("effective-kotlin")
        advanceUntilIdle()

        val event = analytics.events.single { it.name == "book_favorite_toggled" }
        assertEquals("effective-kotlin", event.params["book_id"])
    }

    @Test
    fun loginViewModel_successTracksProviderAndAuthenticates() = runTest {
        val analytics = FakeAnalyticsService()
        val user = AuthUser("token", "Test User", AuthProvider.YANDEX)
        val viewModel = LoginViewModel(
            authService = FakeAuthService(AuthResult.Success(user)),
            analytics = analytics
        )

        viewModel.login(object : Activity() {}, AuthProvider.YANDEX)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isAuthenticated)
        val event = analytics.events.single { it.name == "user_logged_in" }
        assertEquals("yandex", event.params["provider"])
    }

    @Test
    fun loginViewModel_errorDoesNotAuthenticateOrTrackLogin() = runTest {
        val analytics = FakeAnalyticsService()
        val viewModel = LoginViewModel(
            authService = FakeAuthService(AuthResult.Error("failed")),
            analytics = analytics
        )

        viewModel.login(object : Activity() {}, AuthProvider.VK)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isAuthenticated)
        assertTrue(analytics.events.none { it.name == "user_logged_in" })
        assertEquals("failed", viewModel.state.value.message)
    }

    private fun booksViewModel(analytics: FakeAnalyticsService): BooksViewModel {
        val repository = InMemoryBookRepository(BookSeed.books)
        return BooksViewModel(
            getBooks = GetBooksUseCase(repository),
            getBookDetails = GetBookDetailsUseCase(repository),
            getFavorites = GetFavoriteBooksUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
            searchBooks = SearchBooksUseCase(),
            analytics = analytics
        )
    }
}
