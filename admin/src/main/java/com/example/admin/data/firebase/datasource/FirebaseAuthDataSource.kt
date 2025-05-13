package com.example.admin.data.firebase.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
class FirebaseAuthDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, password: String): Flow<Result<FirebaseUser>> = flow {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        emit(Result.success(result.user!!))
    }.catch { emit(Result.failure(it)) }

    suspend fun getUserRole(uid: String): String? {
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.getString("role")
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}
