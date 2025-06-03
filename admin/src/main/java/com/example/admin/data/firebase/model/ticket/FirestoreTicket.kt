package com.example.admin.data.firebase.model.ticket

import java.util.Date

data class FirestoreTicket(
    val ticketId: String = "",
    val seatLabel: String = "",
    val type: String = "NORMAL",
    val price: Double = 0.0,
    val status: String = "available",
    val bookingTime: Date? = null,
    val userId: String? = null,

    val regionName: String = "",
    val districtName: String = "",        // Quận 1
    val roomName: String = "",            // Room 3
    val screenType: String = "",          // 2D / 3D / IMAX
    val screenCategory: String = "",      // Early, Regular, Late

    val movieName: String = "",
    val showtimeId: String = "",
    val movieId: String = "",
    val startTime: Date? = null,
    val endTime: Date? = null,

    val row: Int = 0,


)
