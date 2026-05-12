package com.example.lab5

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lab5.analytics.AppMetricaAnalyticsService
import com.example.lab5.analytics.AnalyticsService
import com.example.lab5.auth.EncryptedAuthStorage
import com.example.lab5.auth.SdkAuthService
import com.example.lab5.config.FirebaseRemoteConfigService
import com.example.lab5.config.NoopRemoteConfigService
import com.example.lab5.config.RemoteConfigService
import com.example.lab5.data.InMemoryBookRepository
import com.example.lab5.domain.usecase.GetBookDetailsUseCase
import com.example.lab5.domain.usecase.GetBooksUseCase
import com.example.lab5.domain.usecase.GetFavoriteBooksUseCase
import com.example.lab5.domain.usecase.SearchBooksUseCase
import com.example.lab5.domain.usecase.ToggleFavoriteUseCase
import com.example.lab5.messaging.FcmTokenStorage
import com.example.lab5.profile.FirebaseUserProfileService
import com.example.lab5.profile.NoopUserProfileService
import com.example.lab5.profile.UserProfileService
import com.example.lab5.ui.auth.LoginViewModel
import com.example.lab5.ui.books.BooksViewModel
import com.example.lab5.ui.profile.ProfileViewModel

object AppContainer {
    private lateinit var appContext: Context

    private val repository = InMemoryBookRepository()
    private val analyticsService: AnalyticsService = AppMetricaAnalyticsService()
    private val authService: SdkAuthService by lazy {
        SdkAuthService(EncryptedAuthStorage(appContext))
    }
    private val tokenStorage: FcmTokenStorage by lazy {
        FcmTokenStorage(appContext)
    }
    private val remoteConfigService: RemoteConfigService by lazy {
        if (BuildConfig.HAS_GOOGLE_SERVICES) FirebaseRemoteConfigService() else NoopRemoteConfigService()
    }
    private val profileService: UserProfileService by lazy {
        if (BuildConfig.HAS_GOOGLE_SERVICES) FirebaseUserProfileService(tokenStorage) else NoopUserProfileService()
    }

    private val getBooksUseCase = GetBooksUseCase(repository)
    private val getBookDetailsUseCase = GetBookDetailsUseCase(repository)
    private val getFavoriteBooksUseCase = GetFavoriteBooksUseCase(repository)
    private val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
    private val searchBooksUseCase = SearchBooksUseCase()

    fun initialize(context: Context) {
        appContext = context.applicationContext
        remoteConfigService.initialize()
    }

    val booksViewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return BooksViewModel(
                getBooks = getBooksUseCase,
                getBookDetails = getBookDetailsUseCase,
                getFavorites = getFavoriteBooksUseCase,
                toggleFavorite = toggleFavoriteUseCase,
                searchBooks = searchBooksUseCase,
                analytics = analyticsService
            ) as T
        }
    }

    val loginViewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return LoginViewModel(
                authService = authService,
                analytics = analyticsService
            ) as T
        }
    }

    val profileViewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return ProfileViewModel(
                profileService = profileService,
                remoteConfigService = remoteConfigService
            ) as T
        }
    }
}
