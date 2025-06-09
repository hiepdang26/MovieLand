package com.example.admin.ui.features.voucher.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseVoucherDataSource
import com.example.admin.data.firebase.model.voucher.FirestoreVoucher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddVoucherViewModel @Inject constructor(
    private val voucherDataSource: FirebaseVoucherDataSource
) : ViewModel() {

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun clearError() {
        _error.value = null
    }

    fun addVoucher(voucher: FirestoreVoucher) {
        viewModelScope.launch {
            val result = voucherDataSource.addVoucher(voucher)
            if (result.isSuccess) {
                _success.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Lỗi không xác định"
            }
        }
    }
}