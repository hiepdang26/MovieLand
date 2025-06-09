package com.example.admin.ui.features.voucher.edit

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseVoucherDataSource
import com.example.admin.data.firebase.model.voucher.FirestoreVoucher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditVoucherViewModel @Inject constructor(
    private val voucherDataSource: FirebaseVoucherDataSource
) : ViewModel() {

    private val _voucher = MutableStateFlow<FirestoreVoucher?>(null)
    val voucher: StateFlow<FirestoreVoucher?> = _voucher

    private val _updateSuccess = MutableSharedFlow<Boolean>()
    val updateSuccess: SharedFlow<Boolean> = _updateSuccess

    private val _deleteSuccess = MutableSharedFlow<Boolean>()
    val deleteSuccess: SharedFlow<Boolean> = _deleteSuccess

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    fun loadVoucher(voucherId: String) {
        viewModelScope.launch {
            val result = voucherDataSource.getVoucherById(voucherId)
            result.onSuccess { _voucher.value = it }
            result.onFailure { _error.emit("Không thể tải voucher: ${it.message}") }
        }
    }

    fun updateVoucher(id: String, update: Map<String, Any?>) {
        viewModelScope.launch {
            val result = voucherDataSource.updateVoucher(id, update)
            if (result.isSuccess) _updateSuccess.emit(true)
            else _error.emit("Cập nhật thất bại: ${result.exceptionOrNull()?.message}")
        }
    }

    fun deleteVoucher(id: String) {
        viewModelScope.launch {
            val result = voucherDataSource.deleteVoucher(id)
            if (result.isSuccess) _deleteSuccess.emit(true)
            else _error.emit("Xoá thất bại: ${result.exceptionOrNull()?.message}")
        }
    }
}
