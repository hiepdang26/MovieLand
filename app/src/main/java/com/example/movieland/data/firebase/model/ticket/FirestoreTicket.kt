package com.example.movieland.data.firebase.model.ticket

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class FirestoreTicket(
    val ticketId: String = "",
    val seatLabel: String = "",
    val type: String = "NORMAL",
    val price: Double = 0.0,
    val status: String = "available",
    val bookingTime: Date? = null,
    val userId: String? = null,

    val regionName: String = "",
    val districtName: String = "",
    val roomName: String = "",
    val screenType: String = "",
    val screenCategory: String = "",

    val movieName: String = "",
    val showtimeId: String = "",
    val movieId: String = "",
    val startTime: Date? = null,
    val endTime: Date? = null,
): Parcelable
