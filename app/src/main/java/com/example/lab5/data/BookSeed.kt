package com.example.lab5.data

import com.example.lab5.domain.model.Book
import com.example.lab5.domain.model.ReadingStatus

object BookSeed {
    val books = listOf(
        Book(
            id = "clean-architecture",
            title = "Clean Architecture",
            author = "Robert C. Martin",
            description = "Практическое руководство по устойчивой архитектуре приложений и разделению ответственности.",
            genre = "Architecture",
            year = 2017,
            readingStatus = ReadingStatus.Reading,
            rating = 4.8,
            isFavorite = true
        ),
        Book(
            id = "effective-kotlin",
            title = "Effective Kotlin",
            author = "Marcin Moskala",
            description = "Книга про идиоматичный Kotlin, читаемость, безопасность и лучшие практики разработки.",
            genre = "Kotlin",
            year = 2024,
            readingStatus = ReadingStatus.Planned,
            rating = 4.7,
            isFavorite = false
        ),
        Book(
            id = "android-development-patterns",
            title = "Android Development Patterns",
            author = "Alex Forrester",
            description = "Подборка подходов к построению Android-приложений, модульности и поддерживаемости.",
            genre = "Android",
            year = 2023,
            readingStatus = ReadingStatus.Reading,
            rating = 4.4,
            isFavorite = true
        ),
        Book(
            id = "domain-driven-design",
            title = "Domain-Driven Design Distilled",
            author = "Vaughn Vernon",
            description = "Краткое и прикладное введение в DDD с акцентом на модели и границы контекстов.",
            genre = "Design",
            year = 2016,
            readingStatus = ReadingStatus.Finished,
            rating = 4.6,
            isFavorite = false
        ),
        Book(
            id = "refactoring",
            title = "Refactoring",
            author = "Martin Fowler",
            description = "Классика про безопасное улучшение кода, работу с техдолгом и небольшие понятные изменения.",
            genre = "Engineering",
            year = 2018,
            readingStatus = ReadingStatus.Finished,
            rating = 4.9,
            isFavorite = true
        ),
        Book(
            id = "pragmatic-programmer",
            title = "The Pragmatic Programmer",
            author = "David Thomas",
            description = "Советы по профессиональной разработке, принятию решений и долгосрочному росту инженера.",
            genre = "Engineering",
            year = 2019,
            readingStatus = ReadingStatus.Planned,
            rating = 4.8,
            isFavorite = false
        ),
        Book(
            id = "jetpack-compose-essentials",
            title = "Jetpack Compose Essentials",
            author = "Rajesh Kumar",
            description = "Практический справочник по Compose: состояния, layout, material-компоненты и производительность.",
            genre = "Compose",
            year = 2024,
            readingStatus = ReadingStatus.Reading,
            rating = 4.3,
            isFavorite = false
        ),
        Book(
            id = "designing-data-intensive-applications",
            title = "Designing Data-Intensive Applications",
            author = "Martin Kleppmann",
            description = "Глубокий разбор хранения, потоков данных, согласованности и масштабирования систем.",
            genre = "Systems",
            year = 2017,
            readingStatus = ReadingStatus.Planned,
            rating = 5.0,
            isFavorite = false
        )
    )
}
