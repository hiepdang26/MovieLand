package com.example.movieland.data.firebase.model.ticket

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class SelectedCombo(
    val comboId: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
): Parcelable