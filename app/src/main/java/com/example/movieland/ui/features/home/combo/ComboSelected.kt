package com.example.movieland.ui.features.home.combo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComboSelected(
    val comboId: String,
    val comboName: String,
    val comboPrice: Double,
    val comboImageUrl: String,
    val quantity: Int
) : Parcelable
