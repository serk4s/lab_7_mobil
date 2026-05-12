package com.example.lab5.ui.navigation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.ui.BookScaffold
import com.example.lab5.AppContainer
import com.example.lab5.ui.about.AboutScreen
import com.example.lab5.ui.auth.LoginScreen
import com.example.lab5.ui.auth.LoginViewModel
import com.example.lab5.ui.books.BookDetailsScreen
import com.example.lab5.ui.books.BooksScreen
import com.example.lab5.ui.books.BooksViewModel
import com.example.lab5.ui.profile.ProfileViewModel

@Composable
fun BookShelfApp(
    notificationTarget: NotificationNavigationTarget? = null,
    onNotificationTargetHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    val viewModel: BooksViewModel = viewModel(factory = AppContainer.booksViewModelFactory)
    val loginViewModel: LoginViewModel = viewModel(factory = AppContainer.loginViewModelFactory)
    val profileViewModel: ProfileViewModel = viewModel(factory = AppContainer.profileViewModelFactory)
    val loginState by loginViewModel.state.collectAsStateWithLifecycle()
    val catalogState by viewModel.catalogState.collectAsStateWithLifecycle()
    val favoritesState by viewModel.favoritesState.collectAsStateWithLifecycle()
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()
    val remoteConfigState by profileViewModel.remoteConfigState.collectAsStateWithLifecycle()
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    if (!loginState.isAuthenticated) {
        LoginScreen(
            state = loginState,
            onLoginClick = loginViewModel::login
        )
        return
    }

    LaunchedEffect(loginState.userName, loginState.email) {
        profileViewModel.syncAuthenticatedUser(loginState.userName, loginState.email)
    }

    LaunchedEffect(loginState.isAuthenticated) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(notificationTarget, loginState.isAuthenticated) {
        notificationTarget?.let { target ->
            when {
                !target.bookId.isNullOrBlank() || target.target == "details" -> {
                    target.bookId?.takeIf { it.isNotBlank() }?.let {
                        navController.navigate(AppRoute.Details.build(it))
                    } ?: navController.navigate(AppRoute.Catalog.route)
                }

                target.target == AppRoute.Favorites.route -> navController.navigate(AppRoute.Favorites.route)
                target.target == AppRoute.About.route || target.target == "profile" -> {
                    navController.navigate(AppRoute.About.route)
                }

                else -> navController.navigate(AppRoute.Catalog.route)
            }
            onNotificationTargetHandled()
        }
    }

    BookScaffold(
        title = "Книжная полка",
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Catalog.route
        ) {
            composable(AppRoute.Catalog.route) {
                LaunchedEffect(Unit) {
                    viewModel.trackScreenViewed("catalog")
                }
                BooksScreen(
                    state = catalogState,
                    welcomeBannerText = remoteConfigState.welcomeBannerText,
                    experimentalFeatureEnabled = remoteConfigState.experimentalFeatureEnabled,
                    onQueryChange = viewModel::onQueryChange,
                    onBookClick = { navController.navigate(AppRoute.Details.build(it)) },
                    onFavoriteToggle = viewModel::toggleFavorite,
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                )
            }
            composable(AppRoute.Favorites.route) {
                LaunchedEffect(Unit) {
                    viewModel.trackScreenViewed("favorites")
                }
                BooksScreen(
                    state = favoritesState,
                    welcomeBannerText = remoteConfigState.welcomeBannerText,
                    experimentalFeatureEnabled = remoteConfigState.experimentalFeatureEnabled,
                    onQueryChange = viewModel::onQueryChange,
                    onBookClick = { navController.navigate(AppRoute.Details.build(it)) },
                    onFavoriteToggle = viewModel::toggleFavorite,
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                )
            }
            composable(AppRoute.About.route) {
                LaunchedEffect(Unit) {
                    viewModel.trackScreenViewed("about")
                }
                AboutScreen(
                    userName = loginState.userName,
                    provider = loginState.provider,
                    login = loginState.login,
                    firstName = loginState.firstName,
                    lastName = loginState.lastName,
                    email = loginState.email,
                    firebaseProfile = profileState.profile,
                    remoteConfigState = remoteConfigState,
                    profileMessage = profileState.message,
                    isProfileLoading = profileState.isLoading,
                    onLogoutClick = loginViewModel::logout,
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                )
            }
            composable(AppRoute.Details.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId").orEmpty()
                LaunchedEffect(bookId) {
                    viewModel.trackScreenViewed("book_details")
                }
                val detailsState by viewModel.detailsState(bookId).collectAsStateWithLifecycle()
                BookDetailsScreen(
                    state = detailsState,
                    onBackClick = navController::popBackStack,
                    onFavoriteToggle = viewModel::toggleFavorite,
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem(AppRoute.Catalog.route, "Каталог", Icons.AutoMirrored.Outlined.MenuBook),
        NavigationItem(AppRoute.Favorites.route, "Избранное", Icons.Outlined.Bookmarks),
        NavigationItem(AppRoute.About.route, "Профиль", Icons.Outlined.Person)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
