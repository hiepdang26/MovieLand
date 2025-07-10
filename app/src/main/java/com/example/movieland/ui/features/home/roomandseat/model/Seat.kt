package com.example.movieland.ui.features.home.roomandseat.model

import com.example.movieland.data.firebase.model.ticket.FirestoreTicket

data class Seat(
    var row: Int,
    var position: Int,
    val column: Int,
    val label: String,
    var type: SeatType = SeatType.NORMAL,
    var isSelected: Boolean = false,
    val isDummy: Boolean = false,
    var ticket: FirestoreTicket? = null

) {
    enum class SeatType {
        NORMAL, VIP
    }

    data class SeatRow(
        val rowNumber: Int,
        val seats: List<Seat>
    )
}