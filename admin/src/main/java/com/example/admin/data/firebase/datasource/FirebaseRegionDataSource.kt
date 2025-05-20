package com.example.admin.data.firebase.datasource


import android.content.Context
import android.util.Log
import com.example.admin.data.firebase.model.region.FirestoreRegion
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRegionDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val appContext: Context
) {

    suspend fun uploadRegion(region: FirestoreRegion): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (region.id.isBlank()) {
                // Tạo mới
                firestore.collection("regions").document()
            } else {
                // Cập nhật
                firestore.collection("regions").document(region.id)
            }
            // Nếu tạo mới, gán lại id cho đối tượng trước khi lưu
            val regionToSave = if (region.id.isBlank()) {
                region.copy(id = docRef.id)
            } else {
                region
            }
            docRef.set(regionToSave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRegionDataSource", "Lỗi khi upload region: ${e.message}", e)
            Result.failure(e)
        }
    }


    fun getAllRegions(): Flow<List<FirestoreRegion>> = callbackFlow {
        try {
            val listener = firestore.collection("regions")
                .orderBy("name")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val result = snapshot?.toObjects(FirestoreRegion::class.java) ?: emptyList()
                    val wasSent = trySend(result).isSuccess
                    if (!wasSent) {
                        Log.w("FirebaseRegionDataSource", "Flow không gửi được dữ liệu.")
                    }
                }

            awaitClose { listener.remove() }

        } catch (e: Exception) {
            Log.e("FirebaseRegionDataSource", "Lỗi khi listen regions: ${e.message}", e)
            close(e)
        }
    }

    suspend fun getRegionById(regionId: String): Result<FirestoreRegion> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("regions").document(regionId).get().await()
            val region = snapshot.toObject(FirestoreRegion::class.java)
                ?: return@withContext Result.failure(Exception("Không tìm thấy nơi với ID: $regionId"))
            Result.success(region)
        } catch (e: Exception) {
            Log.e("FirebaseRegionDataSource", "Lỗi khi get region by id: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateRegion(regionId: String, updatedData: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("regions").document(regionId).update(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRegionDataSource", "Lỗi khi update region: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteRegion(regionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("regions").document(regionId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRegionDataSource", "Lỗi khi delete region: ${e.message}", e)
            Result.failure(e)
        }
    }
}
