package com.example.movieland.ui.features.personal.ticket

import com.example.movieland.data.firebase.model.ticket.FirestoreTicket

data class BookingGroup(
    val bookingId: String,
    val tickets: List<FirestoreTicket>
)
