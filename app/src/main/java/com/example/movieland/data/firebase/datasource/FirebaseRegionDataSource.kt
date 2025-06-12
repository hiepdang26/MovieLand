package com.example.movieland.data.firebase.datasource


import android.content.Context
import android.util.Log
import com.example.movieland.data.firebase.model.region.FirestoreRegion
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

}
