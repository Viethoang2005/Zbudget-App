
package com.example.zbudget.data.repository

import com.google.firebase.auth.EmailAuthProvider
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

        val savedName = snapshot.getString("displayName")?.trim()

        val displayName = savedName
            ?.takeIf {
                it.isNotBlank() && it != "Người dùng"
            }
            ?: user.displayName?.trim()?.takeIf {
                it.isNotBlank()
            }
            ?: user.email?.substringBefore("@")?.trim()?.takeIf {
                it.isNotBlank()
            }
            ?: "Người dùng"

        userRef.set(
            mapOf(
                "displayName" to displayName,
                "email" to user.email.orEmpty()
            ),
            SetOptions.merge()
        ).await()
    }
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ) {
        val user = firebaseAuth.currentUser
            ?: throw Exception("Không tìm thấy tài khoản")

        val email = user.email
            ?: throw Exception("Tài khoản không có email")

        val hasPasswordProvider = user.providerData.any {
            it.providerId == EmailAuthProvider.PROVIDER_ID
        }

        if (!hasPasswordProvider) {
            throw Exception("Tài khoản Google không hỗ trợ đổi mật khẩu tại đây")
        }

        val credential = EmailAuthProvider.getCredential(
            email,
            currentPassword
        )

        user.reauthenticate(credential).await()
        user.updatePassword(newPassword).await()
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
