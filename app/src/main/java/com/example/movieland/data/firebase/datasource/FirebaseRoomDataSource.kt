package com.example.movieland.data.firebase.datasource

import android.util.Log
import com.example.movieland.data.firebase.model.room.FirestoreRoom
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
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

    suspend fun getTicketsByShowtime(showtimeId: String): Result<List<FirestoreTicket>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore
                .collection("showtimes")
                .document(showtimeId)
                .collection("tickets")
                .get()
                .await()

            val tickets = snapshot.documents.mapNotNull {
                it.toObject(FirestoreTicket::class.java)?.copy(ticketId = it.id)
            }

            Result.success(tickets)
        } catch (e: Exception) {
            Log.e("FirebaseRoomDataSource", "Lỗi khi lấy danh sách vé: ${e.message}", e)
            Result.failure(e)
        }
    }

}
