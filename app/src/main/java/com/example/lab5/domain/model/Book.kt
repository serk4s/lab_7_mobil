package com.example.lab5.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val genre: String,
    val year: Int,
    val readingStatus: ReadingStatus,
    val rating: Double,
    val isFavorite: Boolean
)
