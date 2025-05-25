package com.example.admin.data.firebase.model.room
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
) {
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "id" to id,
            "districtId" to districtId,
            "districtName" to districtName,
            "name" to name,
            "totalSeats" to totalSeats,
            "seatInRow" to seatInRow,
            "layoutJson" to layoutJson,
        )
        createdAt?.let { map["createdAt"] = it }
        updatedAt?.let { map["updatedAt"] = it }
        return map
    }
}
