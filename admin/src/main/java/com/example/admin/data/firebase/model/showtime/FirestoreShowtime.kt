package com.example.admin.data.firebase.model.showtime

import java.util.Date

data class FirestoreShowtime(
    val id: String = "",
    val roomId: String = "",
    val movieId: String = "",
    val movieName: String = "",
    val startTime: Date? = null,  // Thời gian bắt đầu chiếu
    val endTime: Date? = null,    // Thời gian kết thúc chiếu
    val date: Date? = null,       // Ngày chiếu riêng (có thể để null nếu ko dùng)
    val availableSeats: Int = 0,
    val bookedSeats: List<String> = emptyList(),
    val price: Double = 0.0,      // Giá vé suất chiếu
    val status: String = "active", // trạng thái: active, canceled, ended
    val theaterId: String = "",   // ID rạp chiếu (nếu có)
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)

