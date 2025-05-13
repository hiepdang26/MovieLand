package com.example.movieland.data.firebase.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phone: String,
        birthdate: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User is null")

            val userData = mapOf(
                "email" to email,
                "name" to name,
                "phone" to phone,
                "birthdate" to birthdate,
                "role" to "user"
            )

            firestore.collection("users").document(user.uid).set(userData).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getUserRole(uid: String): String? {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.getString("role")
        } catch (e: Exception) {
            null
        }
    }
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Đăng xuất
    fun logout() {
        auth.signOut()
    }


}
