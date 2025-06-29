package com.example.movieland.ui.features.home.payment.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailPaymentViewModel @Inject constructor(): ViewModel() {
    fun fetchTicketsByBookingId(bookingId: String, callback: (List<FirestoreTicket>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collectionGroup("tickets")
            .whereEqualTo("bookingId", bookingId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tickets = querySnapshot.documents.mapNotNull { it.toObject(FirestoreTicket::class.java) }
                // Log từng vé
                tickets.forEach { ticket ->
                    Log.d("DetailPaymentVM", "Ticket: $ticket")
                }
                Log.d("DetailPaymentVM", "Tổng vé lấy được: ${tickets.size}")
                callback(tickets)
            }
            .addOnFailureListener { e ->
                Log.e("DetailPaymentVM", "Lỗi khi fetch tickets: ${e.message}", e)
            }
    }
}
