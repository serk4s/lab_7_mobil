package com.example.lab5.auth

import kotlinx.coroutines.CompletableDeferred

object YandexAuthBridge {
    private var deferred: CompletableDeferred<AuthResult>? = null

    fun prepare(): CompletableDeferred<AuthResult> {
        deferred?.complete(AuthResult.Cancelled)
        return CompletableDeferred<AuthResult>().also { deferred = it }
    }

    fun complete(result: AuthResult) {
        deferred?.complete(result)
        deferred = null
    }
}
