package com.example.movieland.ui.features.personal.ticket

import android.os.Parcelable
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingGroup(
    val bookingId: String,
    val tickets: List<FirestoreTicket>
): Parcelable
