package com.example.movieland.data.firebase.datasource

import com.example.movieland.data.firebase.model.combo.FirestoreCombo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseComboDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun loadCombosByDistrict(districtId: String): Result<List<FirestoreCombo>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = firestore.collection("combos")
                .whereEqualTo("districtId", districtId)
                .get().await()

            val combos = snapshot.toObjects(FirestoreCombo::class.java)
            Result.success(combos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
