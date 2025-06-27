package com.example.movieland.data.firebase.datasource

import android.util.Log
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseTicketDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    suspend fun getTickets(showtimeId: String): Result<List<FirestoreTicket>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val snapshot =
                    firestore.collection("showtimes").document(showtimeId).collection("tickets")
                        .get().await()

                val tickets = snapshot.toObjects(FirestoreTicket::class.java)
                Result.success(tickets)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getBookedTicketsForUser(userId: String): Result<List<FirestoreTicket>> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collectionGroup("tickets")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("status", "booked")
                    .get()
                    .await()
                val tickets = snapshot.toObjects(FirestoreTicket::class.java)
                Result.success(tickets)
            } catch (e: Exception) {
                Log.e("FirebaseTicketDataSource", "Error getBookedTicketsForUser: ${e.message}", e)
                Result.failure(e)
            }
        }


}
