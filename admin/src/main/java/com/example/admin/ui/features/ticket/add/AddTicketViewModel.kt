package com.example.admin.ui.features.ticket.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseTicketDataSource
import com.example.admin.data.firebase.model.ticket.FirestoreTicket
import com.example.admin.data.firebase.model.room.FirestoreRoom
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddTicketViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val ticketDataSource: FirebaseTicketDataSource
) : ViewModel() {

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun clearError() {
        _error.value = null
    }

    fun generateTickets(
        showtimeId: String,
        roomId: String,
        normalPrice: Double,
        vipPrice: Double
    ) {
        viewModelScope.launch {
            try {
                val roomSnap = firestore.collection("rooms").document(roomId).get().await()
                val room = roomSnap.toObject(FirestoreRoom::class.java)
                    ?: throw Exception("Room không tồn tại")

                val showtimeSnap = firestore.collection("showtimes")
                    .document(showtimeId)
                    .get()
                    .await()
                val showtime = showtimeSnap.toObject(FirestoreShowtime::class.java)
                    ?: throw Exception("Showtime không tồn tại")

                val layoutData = parseLayoutJson(room.layoutJson)
                val tickets = layoutData.map { seat ->
                    val seatPrice = when (seat.type.uppercase()) {
                        "NORMAL" -> normalPrice
                        "VIP" -> vipPrice
                        else -> normalPrice
                    }

                    FirestoreTicket(
                        ticketId = UUID.randomUUID().toString(),
                        seatLabel = seat.label,
                        type = seat.type,
                        price = seatPrice,
                        status = "available",
                        bookingTime = null,
                        userId = null,

                        regionName = "",
                        districtName = showtime.districtName,
                        roomName = showtime.roomName,
                        screenType = showtime.screenType,
                        screenCategory = showtime.screenCategory,

                        movieName = showtime.movieName,
                        movieId = showtime.movieId,
                        showtimeId = showtime.id,
                        showDate = showtime.date,
                        startTime = showtime.startTime,
                        endTime = showtime.endTime,

                        combos = emptyList()
                    )
                }

                ticketDataSource.generateTicketsForShowtime(showtimeId, tickets)
                _success.value = true

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun parseLayoutJson(json: String): List<Seat> {
        val layout = Gson().fromJson(json, LayoutWrapper::class.java)
        return layout.seats
    }

    data class LayoutWrapper(
        val rows: Int,
        val columns: Int,
        val seats: List<Seat>
    )

    data class Seat(
        val row: Int,
        val column: Int,
        val label: String,
        val type: String
    )
}
