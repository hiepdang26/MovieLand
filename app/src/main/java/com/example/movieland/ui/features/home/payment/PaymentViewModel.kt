package com.example.movieland.ui.features.home.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseShowtimeDataSource
import com.example.movieland.data.firebase.datasource.FirebaseVoucherDataSource
import com.example.movieland.data.firebase.model.showtime.FirestoreShowtime
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val showtimeDataSource: FirebaseShowtimeDataSource,
    private val voucherDataSource: FirebaseVoucherDataSource

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
}
