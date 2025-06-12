package com.example.movieland.data.firebase.model.showtime

import java.util.Date

data class FirestoreShowtime(
    val id: String = "",
    val districtId: String = "",
    val districtName: String = "",
    val roomId: String = "",
    val roomName : String = "",
    val movieId: String = "",
    val movieName: String = "",
    val startTime: Date? = null,
    val endTime: Date? = null,
    val date: Date? = null,
    val totalSeat: Int = 0,
    val seatInEachRow: Int = 0,
    val availableSeats: Int = 0,
    val bookedSeats: List<String> = emptyList(),
    val price: Double = 0.0,
    val screenType: String = "",
    val screenCategory: String = "",
    val status: String = "",
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)

