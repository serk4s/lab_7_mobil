package com.example.lab5.ui.navigation

import android.content.Intent

data class NotificationNavigationTarget(
    val target: String?,
    val bookId: String?
) {
    companion object {
        fun fromIntent(intent: Intent?): NotificationNavigationTarget? {
            val extras = intent?.extras ?: return null
            val target = extras.getString("target")
            val bookId = extras.getString("bookId")
            if (target.isNullOrBlank() && bookId.isNullOrBlank()) return null
            return NotificationNavigationTarget(target = target, bookId = bookId)
        }
    }
}
