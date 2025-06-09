package com.example.admin.data.firebase.model.combo


import java.util.Date

data class FirestoreCombo(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,

    val districtId: String? = null,
    val districtName: String? = null,

    val isAvailable: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
