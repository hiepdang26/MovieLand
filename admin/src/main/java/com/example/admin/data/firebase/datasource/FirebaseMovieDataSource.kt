package com.example.admin.data.firebase.datasource

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.admin.data.firebase.model.FirestoreMovie
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

class FirebaseMovieDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val appContext: Context

) {

    suspend fun uploadMovie(movie: FirestoreMovie): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("movies")
                .document(movie.id.toString())
                .set(movie)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
    suspend fun uploadImageToStorage(uri: Uri): String = withContext(Dispatchers.IO) {
        val context = appContext

        Log.d("FirebaseUpload", "Bắt đầu mở input stream từ uri: $uri")

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("❌ Không thể mở input stream từ uri: $uri")

        Log.d("FirebaseUpload", "✅ Mở được input stream")

        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "posters/${UUID.randomUUID()}.jpg"
        val posterRef = storageRef.child(fileName)

        Log.d("FirebaseUpload", "Bắt đầu upload lên: $fileName")

        try {
            val uploadTask = posterRef.putStream(inputStream)
            uploadTask.await()
            Log.d("FirebaseUpload", "✅ Upload thành công")

            val downloadUrl = posterRef.downloadUrl.await().toString()
            Log.d("FirebaseUpload", "✅ Lấy URL thành công: $downloadUrl")

            return@withContext downloadUrl
        } catch (e: Exception) {
            Log.e("FirebaseUpload", "❌ Upload thất bại: ${e.message}", e)
            throw e
        } finally {
            inputStream.close()
            Log.d("FirebaseUpload", "Đã đóng input stream")
        }
    }




}

