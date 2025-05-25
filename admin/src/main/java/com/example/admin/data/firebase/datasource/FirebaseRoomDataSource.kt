package com.example.admin.data.firebase.datasource

import android.util.Log
import com.example.admin.data.firebase.model.room.FirestoreRoom
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRoomDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collectionName = "rooms"

    suspend fun addRoom(room: FirestoreRoom): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (room.id.isBlank()) {
                firestore.collection(collectionName).document()
            } else {
                firestore.collection(collectionName).document(room.id)
            }
            val roomWithId = room.copy(id = docRef.id)
            docRef.set(roomWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRoomDataSource", "Lỗi khi thêm phòng: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun editRoom(roomId: String, updatedData: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection(collectionName).document(roomId)
                .update(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRoomDataSource", "Lỗi khi cập nhật phòng: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun getRoomsByDistrictId(districtId: String): Flow<List<FirestoreRoom>> = callbackFlow {
        try {
            val listener = firestore.collection(collectionName)
                .whereEqualTo("districtId", districtId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val rooms = snapshot?.toObjects(FirestoreRoom::class.java) ?: emptyList()
                    trySend(rooms).isSuccess
                }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.e("FirebaseRoomDataSource", "Lỗi khi lấy danh sách phòng: ${e.message}", e)
            close(e)
        }
    }

    suspend fun getDetailRoom(roomId: String): Result<FirestoreRoom> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection(collectionName).document(roomId).get().await()
            val room = snapshot.toObject(FirestoreRoom::class.java)
                ?: return@withContext Result.failure(Exception("Không tìm thấy phòng với ID: $roomId"))
            Result.success(room)
        } catch (e: Exception) {
            Log.e("FirebaseRoomDataSource", "Lỗi khi lấy chi tiết phòng: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteRoom(roomId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection(collectionName).document(roomId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRoomDataSource", "Lỗi khi xóa phòng: ${e.message}", e)
            Result.failure(e)
        }
    }
}
