package com.example.admin.ui.features.voucher.show

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
class ShowVoucherViewModel @Inject constructor(
    private val dataSource: FirebaseVoucherDataSource
) : ViewModel() {

    private val _vouchers = MutableStateFlow<List<FirestoreVoucher>>(emptyList())
    val vouchers: StateFlow<List<FirestoreVoucher>> = _vouchers

    fun loadVouchers() {
        viewModelScope.launch {
            val result = dataSource.loadAllVouchers()
            if (result.isSuccess) {
                val vouchers = result.getOrNull() ?: emptyList()
                _vouchers.value = vouchers.sortedByDescending { it.createdAt }
            }
        }
    }

}
