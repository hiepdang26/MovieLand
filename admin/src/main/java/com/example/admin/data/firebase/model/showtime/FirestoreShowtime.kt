package com.example.admin.data.firebase.model.showtime

import java.util.Date

data class FirestoreShowtime(
    val id: String = "",          // ID suất chiếu
    val roomId: String = "",      // ID phòng chiếu
    val movieId: String = "",     // ID phim được chiếu
    val startTime: Date? = null,  // Thời gian bắt đầu chiếu
    val endTime: Date? = null,    // Thời gian kết thúc chiếu
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)
