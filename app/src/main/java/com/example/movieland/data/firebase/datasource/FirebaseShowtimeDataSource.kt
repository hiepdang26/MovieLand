package com.example.movieland.data.firebase.datasource

import android.util.Log
import com.example.movieland.data.firebase.model.showtime.FirestoreShowtime
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

    suspend fun getShowtimesByDistrictAndMovie(
        districtId: String,
        movieId: String
    ): Result<List<FirestoreShowtime>> {
        return try {
            val snapshot = firestore.collection(collectionName)
                .whereEqualTo("districtId", districtId)
                .whereEqualTo("movieId", movieId)
                .get()
                .await()

            val list = snapshot.toObjects(FirestoreShowtime::class.java)
            Result.success(list)
        } catch (e: Exception) {
            Log.e("FirebaseShowtimeDS", "Lỗi lấy showtimes theo district & movie: ${e.message}", e)
            Result.failure(e)
        }
    }


}
