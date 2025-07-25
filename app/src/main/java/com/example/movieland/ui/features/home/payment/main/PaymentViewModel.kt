package com.example.movieland.ui.features.home.payment.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseShowtimeDataSource
import com.example.movieland.data.firebase.datasource.FirebaseVoucherDataSource
import com.example.movieland.data.firebase.model.showtime.FirestoreShowtime
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val showtimeDataSource: FirebaseShowtimeDataSource,
    private val voucherDataSource: FirebaseVoucherDataSource,
    private val firestore: FirebaseFirestore


) : ViewModel() {
    private val _showtimes = MutableStateFlow<FirestoreShowtime?>(null)
    val showtimes: StateFlow<FirestoreShowtime?> = _showtimes

    fun loadShowtimes(showtimeId: String) {
        viewModelScope.launch {
            val result = showtimeDataSource.getDetailShowtime(showtimeId)
            result.onSuccess { showtime ->
                _showtimes.value = showtime
            }.onFailure {
                Log.e("PaymentViewModel", "Lỗi load showtimes: ${it.message}", it)
            }
        }
    }


    private val _vouchers = MutableStateFlow<List<FirestoreVoucher>>(emptyList())
    val vouchers: StateFlow<List<FirestoreVoucher>> = _vouchers

    fun loadVouchers() {
        viewModelScope.launch {
            val result = voucherDataSource.loadAllVouchers()
            if (result.isSuccess) {
                val vouchers = result.getOrNull() ?: emptyList()
                _vouchers.value = vouchers.filter { it.active == true }
            }
        }
    }


    fun updateTicketStatus(
        showtimeId: String, ticketId: String, status: String, userId: String? = null
    ) {
        val docRef = firestore.collection("showtimes").document(showtimeId).collection("tickets")
            .document(ticketId)

        val updateMap = mutableMapOf<String, Any>(
            "status" to status
        )

        if (userId != null) {
            updateMap["userId"] = userId
            updateMap["bookingTime"] = FieldValue.serverTimestamp()
        } else {
            updateMap["userId"] = FieldValue.delete()
            updateMap["bookingTime"] = FieldValue.delete()
        }

        docRef.update(updateMap).addOnSuccessListener {
                Log.d("PaymentViewModel", "Updated ticket $ticketId to $status")
            }.addOnFailureListener { e ->
                Log.e("PaymentViewModel", "Failed to update ticket $ticketId", e)
            }
    }
    fun resetTicketIfLockedByCurrentUser(showtimeId: String, ticketId: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.runTransaction { transaction ->
            val docRef = db.collection("showtimes").document(showtimeId).collection("tickets")
                .document(ticketId)
            val snapshot = transaction.get(docRef)
            val status = snapshot.getString("status")
            val currentUser = snapshot.getString("userId")
            Log.d("resetTicket", "Ticket status=$status, userId=$currentUser, cur=$userId")
            if (status == "locked" && currentUser == userId) {
                transaction.update(docRef, mapOf(
                    "status" to "available",
                    "userId" to null,
                    "bookingTime" to null
                ))
            }
            null
        }.addOnSuccessListener {
            Log.d("resetTicket", "Reset vé $ticketId thành công hoặc không cần reset.")
        }.addOnFailureListener { e ->
            Log.e("resetTicket", "Lỗi khi reset vé $ticketId: ${e.message}")
        }
    }
    fun setTicketsBooking(
        showtimeId: String,
        tickets: List<FirestoreTicket>,
        userId: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.runTransaction { transaction ->
            val docRefs = tickets.map { ticket ->
                db.collection("showtimes").document(showtimeId).collection("tickets")
                    .document(ticket.ticketId)
            }
            val snapshots = docRefs.map { transaction.get(it) }

            snapshots.forEachIndexed { i, snapshot ->
                val ticket = tickets[i]
                if (!snapshot.exists()) {
                    throw Exception("Ghế ${ticket.seatLabel} không tồn tại trên Firestore!")
                }
                val status = snapshot.getString("status")
                val ticketUserId = snapshot.getString("userId")
                if (status != "locked" || ticketUserId != userId) {
                    throw Exception("Ghế ${ticket.seatLabel} đã bị người khác đặt hoặc thay đổi trạng thái!")
                }
            }

            docRefs.forEach {
                transaction.update(it, mapOf("status" to "booking", "userId" to userId))
            }
        }.addOnSuccessListener {
            callback(true, null)
        }.addOnFailureListener { e ->
            callback(false, e.message)
        }
    }


    fun resetBookingToLocked(
        showtimeId: String,
        tickets: List<FirestoreTicket>,
        userId: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.runTransaction { transaction ->
            val snapshotList = tickets.map { ticket ->
                val docRef = db.collection("showtimes").document(showtimeId).collection("tickets")
                    .document(ticket.ticketId)
                val snapshot = transaction.get(docRef)
                ticket to snapshot
            }
            snapshotList.forEach { (ticket, snapshot) ->
                val status = snapshot.getString("status")
                val ticketUserId = snapshot.getString("userId")
                if (status == "booking" && ticketUserId == userId) {
                    transaction.update(
                        snapshot.reference, mapOf(
                            "status" to "locked",
                            "userId" to userId,
                            "bookingTime" to FieldValue.serverTimestamp()
                        )
                    )
                } else {
                    Log.d(
                        "ResetBooking",
                        ">> Không reset ticket ${ticket.ticketId} (status=$status, userId=$ticketUserId)"
                    )
                }
            }
        }.addOnSuccessListener {
            Log.d("ResetBooking", "Reset booking to locked SUCCESS")
            onComplete(true, null)
        }.addOnFailureListener { e ->
            Log.e("ResetBooking", "Reset booking to locked FAIL: ${e.message}")
            onComplete(false, e.message)
        }
    }

    fun setTicketsBooked(
        showtimeId: String,
        bookingId: String,
        tickets: List<FirestoreTicket>,
        userId: String,
        price: Double,
        callback: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val numberOfTickets = tickets.size
        val pricePerTicket = if (numberOfTickets > 0) price / numberOfTickets else 0.0

        db.runTransaction { transaction ->
            tickets.forEachIndexed { index, ticket ->
                val docRef = db.collection("showtimes").document(showtimeId)
                    .collection("tickets").document(ticket.ticketId)

                val ticketPrice = if (index == numberOfTickets - 1) {
                    price - pricePerTicket * (numberOfTickets - 1)
                } else {
                    pricePerTicket
                }

                transaction.update(
                    docRef, mapOf(
                        "status" to "booked",
                        "userId" to userId,
                        "bookingTime" to FieldValue.serverTimestamp(),
                        "bookingId" to bookingId,
                        "price" to ticketPrice,
                        "combos" to ticket.combos.map { combo ->
                            mapOf(
                                "comboId" to combo.comboId,
                                "name" to combo.name,
                                "quantity" to combo.quantity,
                                "price" to combo.price
                            )
                        }
                    )
                )
            }
            val showtimeRef = db.collection("showtimes").document(showtimeId)
            transaction.update(showtimeRef, "availableSeats", FieldValue.increment(-numberOfTickets.toLong()))
        }.addOnSuccessListener {
            callback(true, null)
        }.addOnFailureListener { e ->
            callback(false, e.message)
        }
    }


    fun resetTicketIfLockedAndNotBooked(showtimeId: String, ticketId: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("showtimes").document(showtimeId).collection("tickets").document(ticketId)

        docRef.get().addOnSuccessListener { snapshot ->
            val status = snapshot.getString("status")
            val ticketUserId = snapshot.getString("userId")
            if (status == "locked" && ticketUserId == userId) {
                docRef.update(
                    mapOf(
                        "status" to "available",
                        "userId" to null,
                        "bookingTime" to null
                    )
                )
            }
        }
    }
    fun decreaseVoucherUsage(voucherId: String) {
        viewModelScope.launch {
            try {
                val voucherRef = firestore.collection("vouchers").document(voucherId)
                voucherRef.update("usageLimit", FieldValue.increment(-1))
                    .addOnSuccessListener {
                        Log.d("PaymentViewModel", "Giảm usageLimit thành công")
                    }
                    .addOnFailureListener { e ->
                        Log.e("PaymentViewModel", "Lỗi giảm usageLimit voucher", e)
                    }

            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Lỗi giảm usageLimit voucher", e)
            }
        }
    }


}
