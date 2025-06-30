package com.example.admin.data.firebase.datasource

import android.util.Log
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
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

    suspend fun getShowtimesByRoomAndDate(roomId: String, date: Date): List<FirestoreShowtime> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        calendar.add(Calendar.DATE, 1)
        val endOfDay = calendar.time

        val snapshot = firestore.collection("showtimes")
            .whereEqualTo("roomId", roomId)
            .whereGreaterThanOrEqualTo("startTime", startOfDay)
            .whereLessThan("startTime", endOfDay)
            .get()
            .await()

        val list = snapshot.toObjects(FirestoreShowtime::class.java)

        Log.d("ShowtimeQuery", "Showtimes found: ${list.size}")
        list.forEachIndexed { index, showtime ->
            Log.d(
                "ShowtimeQuery",
                "[$index] Movie: ${showtime.movieName} | startTime: ${showtime.startTime} | endTime: ${showtime.endTime}"
            )
        }
        Log.d("ShowtimeQuery", "Room: $roomId, From: $startOfDay, To: $endOfDay")

        return list
    }
    suspend fun getShowtimesByRoomAndDateExcludeId(roomId: String, date: Date, excludeId: String): List<FirestoreShowtime> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        calendar.add(Calendar.DATE, 1)
        val endOfDay = calendar.time

        val snapshot = firestore.collection("showtimes")
            .whereEqualTo("roomId", roomId)
            .whereGreaterThanOrEqualTo("startTime", startOfDay)
            .whereLessThan("startTime", endOfDay)
            .get()
            .await()

        val list = snapshot.toObjects(FirestoreShowtime::class.java)
            .filter { it.id != excludeId }

        Log.d("ShowtimeQuery", "Showtimes found (exclude): ${list.size}")
        list.forEachIndexed { index, showtime ->
            Log.d(
                "ShowtimeQuery",
                "[$index] Movie: ${showtime.movieName} | startTime: ${showtime.startTime} | endTime: ${showtime.endTime} | id: ${showtime.id}"
            )
        }
        return list
    }


}
