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

    /**
     * Tạo toàn bộ vé cho một suất chiếu từ danh sách FirestoreTicket.
     */
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
                val paddedId = padSeatLabel(ticket.seatLabel)
                val docRef = ticketRef.document(paddedId)
                batch.set(docRef, ticket, SetOptions.merge())
            }

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun padSeatLabel(label: String): String {
        val regex = Regex("([A-Z]+)(\\d+)")
        val match = regex.find(label)
        return if (match != null) {
            val (rowLetter, colNumber) = match.destructured
            "${rowLetter}${colNumber.padStart(2, '0')}" // A1 -> A01
        } else label
    }
    /**
     * Xoá toàn bộ vé trong một suất chiếu.
     */
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

    /**
     * Lấy danh sách tất cả vé trong một suất chiếu.
     */
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

    /**
     * Cập nhật 1 vé theo seatLabel với dữ liệu cụ thể (giá, trạng thái, v.v.)
     */
    suspend fun updateTicket(
        showtimeId: String,
        seatLabel: String,
        data: Map<String, Any?>,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val ticketRef = firestore.collection("showtimes")
                .document(showtimeId)
                .collection("tickets")
                .document(seatLabel)

            ticketRef.update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Đặt lại vé về trạng thái "available" (dùng cho huỷ/thu hồi)
     */
    suspend fun resetTicketStatus(
        showtimeId: String,
        seatLabel: String,
    ): Result<Unit> {
        return updateTicket(
            showtimeId,
            seatLabel,
            mapOf(
                "status" to "available",
                "userId" to null,
                "bookingTime" to null
            )
        )
    }
}
