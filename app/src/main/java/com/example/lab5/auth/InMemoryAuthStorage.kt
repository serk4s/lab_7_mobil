package com.example.lab5.auth

class InMemoryAuthStorage(initialUser: AuthUser? = null) : AuthStorage {
    private var user: AuthUser? = initialUser

    override fun save(user: AuthUser) {
        this.user = user
    }

    override fun get(): AuthUser? = user

    override fun clear() {
        user = null
    }
}
