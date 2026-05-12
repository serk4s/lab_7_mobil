package com.example.lab5.auth

interface AuthStorage {
    fun save(user: AuthUser)
    fun get(): AuthUser?
    fun clear()
}
