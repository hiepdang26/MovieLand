package com.example.movieland.data.firebase.model.room
import java.util.Date

data class FirestoreRoom(
    val id: String = "",
    val districtId: String = "",
    val districtName: String = "",
    val name: String = "",
    val totalSeats: Int = 0,
    val seatInRow: Int = 0,
    val layoutJson: String = "",
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)