package com.example.lab5.ui.navigation

sealed class AppRoute(val route: String) {
    data object Catalog : AppRoute("catalog")
    data object Favorites : AppRoute("favorites")
    data object About : AppRoute("about")
    data object Details : AppRoute("details/{bookId}") {
        fun build(bookId: String): String = "details/$bookId"
    }
}
