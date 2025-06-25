package com.example.movieland.ui.features.home.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseShowtimeDataSource
import com.example.movieland.data.firebase.datasource.FirebaseVoucherDataSource
import com.example.movieland.data.firebase.model.showtime.FirestoreShowtime
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val showtimeDataSource: FirebaseShowtimeDataSource,
    private val voucherDataSource: FirebaseVoucherDataSource,
    private val firestore: FirebaseFirestore


) : ViewModel() {
    private val _showtimes = MutableStateFlow<FirestoreShowtime?>(null)
    val showtimes: StateFlow<FirestoreShowtime?> = _showtimes

    fun loadShowtimes(showtimeId: String) {
        viewModelScope.launch {
            val result = showtimeDataSource.getDetailShowtime(showtimeId)
            result.onSuccess { showtime ->
                _showtimes.value = showtime
            }.onFailure {
                Log.e("PaymentViewModel", "Lỗi load showtimes: ${it.message}", it)
            }
        }
    }


    private val _vouchers = MutableStateFlow<List<FirestoreVoucher>>(emptyList())
    val vouchers: StateFlow<List<FirestoreVoucher>> = _vouchers

    fun loadVouchers() {
        viewModelScope.launch {
            val result = voucherDataSource.loadAllVouchers()
            if (result.isSuccess) {
                _vouchers.value = result.getOrNull() ?: emptyList()
            }
        }
    }

    fun updateTicketStatus(
        showtimeId: String,
        ticketId: String,
        status: String,
        userId: String? = null
    ) {
        val docRef = firestore
            .collection("showtimes")
            .document(showtimeId)
            .collection("tickets")
            .document(ticketId)

        val updateMap = mutableMapOf<String, Any>(
            "status" to status
        )

        if (userId != null) {
            updateMap["userId"] = userId
            updateMap["bookingTime"] = FieldValue.serverTimestamp()
        } else {
            updateMap["userId"] = FieldValue.delete()
            updateMap["bookingTime"] = FieldValue.delete()
        }

        docRef.update(updateMap)
            .addOnSuccessListener {
                Log.d("PaymentViewModel", "Updated ticket $ticketId to $status")
            }
            .addOnFailureListener { e ->
                Log.e("PaymentViewModel", "Failed to update ticket $ticketId", e)
            }
    }
}
