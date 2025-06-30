package com.example.movieland.data.firebase.model.voucher

import java.util.Date

data class FirestoreVoucher(
    val id: String = "",
    val code: String = "",
    val description: String = "",

    val discountType: VoucherType = VoucherType.PERCENTAGE,
    val discountPercent: Double? = null,
    val discountAmount: Double? = null,
    val maxDiscount: Double? = null,

    val minTicketValue: Double = 0.0,
    val applicableWeekdays: List<Int> = emptyList(),
    val usageLimit: Int = 1,
    val usedCount: Int = 0,

    val startDate: Date = Date(),
    val endDate: Date = Date(),

    val active: Boolean = true,

    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
{
    enum class VoucherType {
        PERCENTAGE,
        FIXED
    }
}
