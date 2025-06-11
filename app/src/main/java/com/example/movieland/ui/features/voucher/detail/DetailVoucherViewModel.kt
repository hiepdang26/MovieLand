package com.example.movieland.ui.features.voucher.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseVoucherDataSource
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailVoucherViewModel @Inject constructor(
    private val voucherDataSource: FirebaseVoucherDataSource,
) : ViewModel() {

    private val _voucher = MutableStateFlow<FirestoreVoucher?>(null)
    val voucher: StateFlow<FirestoreVoucher?> = _voucher


    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    fun loadVoucher(voucherId: String) {
        viewModelScope.launch {
            val result = voucherDataSource.getVoucherById(voucherId)
            result.onSuccess { _voucher.value = it }
            result.onFailure { _error.emit("Không thể tải voucher: ${it.message}") }
        }
    }
}