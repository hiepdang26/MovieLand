package com.example.admin.ui.features.region.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseRegionDataSource
import com.example.admin.data.firebase.model.region.FirestoreRegion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRegionViewModel @Inject constructor(
    private val regionDataSource: FirebaseRegionDataSource
) : ViewModel() {

    private val _addResult = MutableStateFlow<Result<Unit>?>(null)
    val addResult: StateFlow<Result<Unit>?> = _addResult

    fun addRegion(name: String) {
        if (name.isBlank()) {
            _addResult.value = Result.failure(Exception("Tên nơi không được để trống"))
            return
        }

        val newRegion = FirestoreRegion(
            id = "",
            name = name,
        )

        viewModelScope.launch {
            val result = regionDataSource.uploadRegion(newRegion)
            _addResult.value = result
        }
    }

    fun resetAddResult() {
        _addResult.value = null
    }
}
