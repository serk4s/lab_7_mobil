package com.example.lab5.profile

import android.util.Log
import com.example.lab5.messaging.FcmTokenStorage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseUserProfileService(
    private val tokenStorage: FcmTokenStorage,
    private val auth: FirebaseAuth = Firebase.auth
) : UserProfileService {
    private val firestore = Firebase.firestore

    override suspend fun syncCurrentProfile(name: String?, email: String?) {
        val userId = ensureUserId()
        val token = fetchFcmToken()
        val profile = mapOf(
            "userId" to userId,
            "name" to name.orEmpty().ifBlank { "Пользователь" },
            "email" to email.orEmpty(),
            "fcmToken" to token,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(profile, SetOptions.merge())
            .awaitResult()
    }

    override fun observeCurrentProfile(): Flow<UserProfile?> = callbackFlow {
        val user = auth.currentUser
        if (user == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        var registration: ListenerRegistration? = firestore.collection(USERS_COLLECTION)
            .document(user.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Profile listener failed", error)
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(UserProfile::class.java))
            }

        awaitClose {
            registration?.remove()
            registration = null
        }
    }

    override fun updateTokenIfUserExists(token: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(
                mapOf(
                    "userId" to userId,
                    "fcmToken" to token,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to update FCM token in Firestore", error)
            }
    }

    private suspend fun ensureUserId(): String {
        auth.currentUser?.let { return it.uid }
        return auth.signInAnonymously().awaitResult().user?.uid
            ?: error("Firebase anonymous auth returned no user")
    }

    private suspend fun fetchFcmToken(): String {
        val token = FirebaseMessaging.getInstance().token.awaitResult()
        tokenStorage.save(token)
        Log.d(TAG, "FCM token: $token")
        return token
    }

    private companion object {
        const val TAG = "FirebaseUserProfile"
        const val USERS_COLLECTION = "users"
    }
}
