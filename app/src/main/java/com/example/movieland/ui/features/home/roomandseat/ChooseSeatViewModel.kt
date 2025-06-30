package com.example.movieland.ui.features.home.roomandseat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseRoomDataSource
import com.example.movieland.data.firebase.model.room.FirestoreRoom
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseSeatViewModel @Inject constructor(
    private val roomDataSource: FirebaseRoomDataSource,
    private val firestore: FirebaseFirestore

) : ViewModel() {

    private val _room = MutableStateFlow<FirestoreRoom?>(null)
    val room: StateFlow<FirestoreRoom?> = _room

    fun loadRoomDetail(roomId: String) {
        viewModelScope.launch {
            val result = roomDataSource.getDetailRoom(roomId)
            result.onSuccess { _room.value = it }
                .onFailure { Log.e("ChooseSeatViewModel", "Lỗi: ${it.message}") }
        }
    }

    fun loadTickets(showtimeId: String, onDone: (List<FirestoreTicket>) -> Unit) {
        viewModelScope.launch {
            val result = roomDataSource.getTicketsByShowtime(showtimeId)
            result.onSuccess { onDone(it) }
                .onFailure { Log.e("ChooseSeatViewModel", "Lỗi tickets: ${it.message}") }
        }
    }


    private var ticketListener: ListenerRegistration? = null

    private val _tickets = MutableStateFlow<List<FirestoreTicket>>(emptyList())
    val tickets: StateFlow<List<FirestoreTicket>> = _tickets

    fun listenTicketsRealtime(showtimeId: String) {
        ticketListener?.remove()

        firestore.collection("showtimes")
            .document(showtimeId)
            .collection("tickets")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("ChooseSeatViewModel", "listenTicketsRealtime error", error)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    val ticketList = snapshots.documents.mapNotNull { it.toObject(FirestoreTicket::class.java) }
                    _tickets.value = ticketList
                    Log.d("ChooseSeatViewModel", "Realtime updated ${ticketList.size} tickets")
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        ticketListener?.remove()
    }

    fun updateTicketStatus(
        showtimeId: String,
        ticketId: String,
        status: String,
        userId: String? = null
    ) {
        val docRef = firestore
            .collection("showtimes")
            .document(showtimeId)
            .collection("tickets")
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

        docRef.update(updateMap)
            .addOnSuccessListener {
                Log.d("ChooseSeatViewModel", "Updated ticket $ticketId to $status")
            }
            .addOnFailureListener { e ->
                Log.e("ChooseSeatViewModel", "Failed to update ticket $ticketId", e)
            }
    }
    fun autoReleaseExpiredTickets(tickets: List<FirestoreTicket>, maxHoldTime: Long) {
        val now = System.currentTimeMillis()
        tickets.forEach { ticket ->
            if (ticket.status == "locked" && ticket.bookingTime != null) {
                val bookingTimeMillis = ticket.bookingTime.time
                if (now - bookingTimeMillis > maxHoldTime) {
                    updateTicketStatus(
                        showtimeId = ticket.showtimeId,
                        ticketId = ticket.ticketId,
                        status = "available",
                        userId = null
                    )
                    Log.d("ChooseSeatFragment", "Reset vé hết hạn: ${ticket.seatLabel}")
                }
            }
        }
    }


}
