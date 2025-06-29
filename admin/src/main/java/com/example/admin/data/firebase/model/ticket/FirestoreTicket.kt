package com.example.admin.data.firebase.model.ticket

import java.util.Date

data class FirestoreTicket(
    val ticketId: String = "",
    val seatLabel: String = "",
    val type: String = "NORMAL",
    val price: Double = 0.0,
    val status: String = "available",
    val bookingTime: Date? = null,
    val bookingId: String? = null,

    val userId: String? = null,

    val regionName: String = "",
    val districtName: String = "",
    val roomName: String = "",
    val screenType: String = "",
    val screenCategory: String = "",

    val movieName: String = "",
    val movieImg: String = "",
    val showtimeId: String = "",
    val showDate: Date?  = null,
    val movieId: String = "",
    val startTime: Date? = null,
    val endTime: Date? = null,

    val combos: List<SelectedCombo> = emptyList()


)
