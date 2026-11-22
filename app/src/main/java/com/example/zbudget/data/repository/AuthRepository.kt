
package com.example.zbudget.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun register(
        email: String,
        password: String
    ): FirebaseUser {
        val user = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
            .user ?: throw Exception("Không thể tạo tài khoản")

        saveUserProfile(user)
        return user
    }

    suspend fun login(
        email: String,
        password: String
    ): FirebaseUser {
        val user = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
            .user ?: throw Exception("Đăng nhập thất bại")

        saveUserProfile(user)
        return user
    }

    suspend fun loginWithGoogle(
        idToken: String
    ): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(
            idToken,
            null
        )

        val user = firebaseAuth
            .signInWithCredential(credential)
            .await()
            .user ?: throw Exception("Đăng nhập Google thất bại")

        saveUserProfile(user)
        return user
    }

    suspend fun saveUserProfile(user: FirebaseUser) {
        val userRef = firestore
            .collection("users")
            .document(user.uid)

        val snapshot = userRef.get().await()

        if (!snapshot.exists() || !snapshot.contains("displayName")) {
            userRef.set(
                mapOf(
                    "displayName" to (
                            user.displayName
                                ?: user.email?.substringBefore("@")
                                ?: "Người dùng"
                            ),
                    "email" to user.email.orEmpty()
                ),
                SetOptions.merge()
            ).await()
        } else {
            userRef.set(
                mapOf("email" to user.email.orEmpty()),
                SetOptions.merge()
            ).await()
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
