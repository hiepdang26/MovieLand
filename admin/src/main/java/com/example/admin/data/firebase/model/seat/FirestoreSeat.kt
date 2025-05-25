package com.example.admin.data.firebase.model.seat

import java.util.Date

data class FirestoreSeat(
    val id: String = "",          // ID ghế
    val roomId: String = "",      // ID phòng ghế thuộc về
    val label: String = "",       // Tên ghế, ví dụ "A1"
    val row: Int = 0,             // Hàng ghế
    val column: Int = 0,          // Cột ghế
    val type: String = "normal",  // Loại ghế: normal, vip, disabled,...
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)