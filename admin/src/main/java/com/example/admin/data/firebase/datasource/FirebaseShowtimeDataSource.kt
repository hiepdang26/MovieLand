package com.example.admin.data.firebase.datasource

import android.util.Log
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseShowtimeDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collectionName = "showtimes"

    suspend fun getAllShowtimesByRoomId(roomId: String): Result<List<FirestoreShowtime>> {
        return try {
            val snapshot = firestore.collection(collectionName)
                .whereEqualTo("roomId", roomId)
                .get()
                .await()

            val list = snapshot.toObjects(FirestoreShowtime::class.java)
            Result.success(list)
        } catch (e: Exception) {
            Log.e("FirebaseShowtimeDS", "Lỗi lấy showtimes: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getDetailShowtime(showtimeId: String): Result<FirestoreShowtime?> {
        return try {
            val doc = firestore.collection(collectionName)
                .document(showtimeId)
                .get()
                .await()

            if (doc.exists()) {
                val showtime = doc.toObject(FirestoreShowtime::class.java)
                Result.success(showtime)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("FirebaseShowtimeDS", "Lỗi lấy chi tiết showtime: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteShowtime(showtimeId: String): Result<Unit> {
        return try {
            firestore.collection(collectionName)
                .document(showtimeId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseShowtimeDS", "Lỗi xóa showtime: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateShowtime(showtimeId: String, updatedData: Map<String, Any?>): Result<Unit> {
        return try {
            firestore.collection(collectionName)
                .document(showtimeId)
                .update(updatedData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseShowtimeDS", "Lỗi cập nhật showtime: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun addShowtime(newShowtime: FirestoreShowtime): Result<String> {
        return try {
            val newId = "showtime_" + UUID.randomUUID().toString()
            val docRef = firestore.collection(collectionName).document(newId)
            docRef.set(newShowtime.copy(id = newId)).await()

            Result.success(newId)
        } catch (e: Exception) {
            Log.e("FirebaseShowtimeDS", "Lỗi thêm mới showtime: ${e.message}", e)
            Result.failure(e)
        }
    }

}
