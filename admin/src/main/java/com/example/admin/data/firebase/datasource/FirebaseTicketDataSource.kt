package com.example.admin.data.firebase.datasource

import com.example.admin.data.firebase.model.ticket.FirestoreTicket
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseTicketDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    suspend fun generateTicketsForShowtime(
        showtimeId: String,
        tickets: List<FirestoreTicket>,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val batch = firestore.batch()
            val ticketRef = firestore.collection("showtimes")
                .document(showtimeId)
                .collection("tickets")

            tickets.forEach { ticket ->
                val docId = "tickets_${ticket.seatLabel}_${ticket.ticketId}"
                val ticketWithDocId = ticket.copy(ticketId = docId)
                val docRef = ticketRef.document(docId)
                batch.set(docRef, ticketWithDocId, SetOptions.merge())
            }

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllTickets(showtimeId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val ticketSnapshot = firestore.collection("showtimes")
                .document(showtimeId)
                .collection("tickets")
                .get()
                .await()

            val batch = firestore.batch()
            ticketSnapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTickets(showtimeId: String): Result<List<FirestoreTicket>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val snapshot = firestore.collection("showtimes")
                    .document(showtimeId)
                    .collection("tickets")
                    .get()
                    .await()

                val tickets = snapshot.toObjects(FirestoreTicket::class.java)
                Result.success(tickets)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    suspend fun updateTicket(
        showtimeId: String,
        seatLabel: String,
        ticketId: String,
        data: Map<String, Any?>,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val docId = "tickets_${seatLabel}_${ticketId}"
            val ticketRef = firestore.collection("showtimes")
                .document(showtimeId)
                .collection("tickets")
                .document(docId)

            ticketRef.update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
