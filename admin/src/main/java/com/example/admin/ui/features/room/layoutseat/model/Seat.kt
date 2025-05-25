package com.example.admin.ui.features.room.layoutseat.model

data class Seat(
    val row: Int,
    val column: Int,
    val label: String,
    var type: SeatType = SeatType.NORMAL,
    var isSelected: Boolean = false,
    val isDummy: Boolean = false // ghế giả để giữ khoảng trống hàng chưa đủ ghế

) {
    enum class SeatType {
        NORMAL, VIP
    }

    data class SeatRow(
        val rowNumber: Int,
        val seats: List<Seat>
    )
}