
package com.example.zbudget.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun register(
        email: String,
        password: String
    ): FirebaseUser {
        return firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
            .user ?: throw Exception("Không thể tạo tài khoản")
    }

    suspend fun login(
        email: String,
        password: String
    ): FirebaseUser {
        return firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
            .user ?: throw Exception("Đăng nhập thất bại")
    }

    suspend fun loginWithGoogle(
        idToken: String
    ): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(
            idToken,
            null
        )

        return firebaseAuth
            .signInWithCredential(credential)
            .await()
            .user ?: throw Exception("Đăng nhập Google thất bại")
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
