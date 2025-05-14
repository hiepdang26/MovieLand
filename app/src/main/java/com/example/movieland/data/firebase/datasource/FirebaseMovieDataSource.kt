package com.example.movieland.data.firebase.datasource

import android.content.Context
import android.util.Log
import com.example.movieland.data.firebase.model.FirestoreMovie
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseMovieDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val appContext: Context

) {

    fun getAllMovies(): Flow<List<FirestoreMovie>> = callbackFlow {
        try {
            val listener = firestore.collection("movies")
                .orderBy("createdAt")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val result = snapshot?.toObjects(FirestoreMovie::class.java) ?: emptyList()
                    val wasSent = trySend(result).isSuccess
                    if (!wasSent) {
                        Log.w("FirebaseMovieDataSource", "Flow không gửi được dữ liệu.")
                    }
                }

            awaitClose { listener.remove() }

        } catch (e: Exception) {
            Log.e("FirebaseMovieDataSource", "Lỗi khi listen movies: ${e.message}", e)
            close(e)
        }
    }

    suspend fun getMovieById(movieId: String): Result<FirestoreMovie> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = firestore.collection("movies").document(movieId).get().await()
            val movie = snapshot.toObject(FirestoreMovie::class.java)
                ?: return@withContext Result.failure(Exception("Không tìm thấy phim với ID: $movieId"))
            Result.success(movie)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

