package com.example.admin.data.firebase.model.seat

import java.util.Date

data class FirestoreSeat(
    val id: String = "",
    val roomId: String = "",
    val label: String = "",
    val row: Int = 0,
    val column: Int = 0,
    val type: String = "normal",
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)