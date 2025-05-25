package com.example.admin.ui.features.region.edit

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
class EditRegionViewModel @Inject constructor(
    private val regionDataSource: FirebaseRegionDataSource
) : ViewModel() {

    private val _region = MutableStateFlow<FirestoreRegion?>(null)
    val region: StateFlow<FirestoreRegion?> = _region

    private val _updateResult = MutableStateFlow<Result<Unit>?>(null)
    val updateResult: StateFlow<Result<Unit>?> = _updateResult

    private val _deleteResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteResult: StateFlow<Result<Unit>?> = _deleteResult

    fun loadRegion(regionId: String) {
        viewModelScope.launch {
            val result = regionDataSource.getRegionById(regionId)
            if (result.isSuccess) {
                _region.value = result.getOrNull()
            } else {
                _region.value = null
            }
        }
    }

    fun updateRegion(name: String) {
        val currentRegion = _region.value
        if (currentRegion == null) {
            _updateResult.value = Result.failure(Exception("Region chưa được load"))
            return
        }
        if (name.isBlank()) {
            _updateResult.value = Result.failure(Exception("Tên vùng không được để trống"))
            return
        }

        val updatedData = mapOf(
            "name" to name
        )

        viewModelScope.launch {
            val result = regionDataSource.updateRegion(currentRegion.id, updatedData)
            _updateResult.value = result
        }
    }

    fun deleteRegion() {
        val currentRegion = _region.value
        if (currentRegion == null) {
            _deleteResult.value = Result.failure(Exception("Region chưa được load"))
            return
        }

        viewModelScope.launch {
            val result = regionDataSource.deleteRegion(currentRegion.id)
            _deleteResult.value = result
        }
    }

    fun resetUpdateResult() {
        _updateResult.value = null
    }

    fun resetDeleteResult() {
        _deleteResult.value = null
    }
}

