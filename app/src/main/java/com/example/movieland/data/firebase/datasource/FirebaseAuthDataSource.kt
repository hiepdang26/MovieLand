package com.example.movieland.data.firebase.datasource

import com.google.firebase.auth.EmailAuthProvider
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
        birthdate: String,
        gender: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User is null")

            val userData = mapOf(
                "email" to email,
                "name" to name,
                "phone" to phone,
                "birthdate" to birthdate,
                "gender" to gender,
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

    suspend fun getCurrentUserData(): Map<String, Any>? {
        val user = auth.currentUser ?: return null
        return try {
            val snapshot = firestore.collection("users").document(user.uid).get().await()
            snapshot.data
        } catch (e: Exception) {
            null
        }
    }
    suspend fun updateCurrentUser(
        name: String,
        gender: String,
        birthdate: String,
        phone: String

    ): Result<Boolean> {
        val user = auth.currentUser ?: return Result.failure(Exception("User is null"))
        return try {
            val updateData = mapOf(
                "name" to name,
                "gender" to gender,
                "birthdate" to birthdate,
                "phone" to phone
            )
            firestore.collection("users").document(user.uid).update(updateData).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCurrentUserWithReauth(email: String, password: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not logged in"))
        return try {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()

            firestore.collection("users").document(user.uid).delete().await()
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun changePassword(email: String, oldPassword: String, newPassword: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not logged in"))
        return try {
            // 1. Re-authenticate
            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            user.reauthenticate(credential).await()

            // 2. Update password
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}
