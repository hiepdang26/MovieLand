package com.example.admin.ui.features.district.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseDistrictDataSource
import com.example.admin.data.firebase.datasource.FirebaseRegionDataSource
import com.example.admin.data.firebase.model.district.FirestoreDistrict
import com.example.admin.data.firebase.model.region.FirestoreRegion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDistrictViewModel @Inject constructor(
    private val districtDataSource: FirebaseDistrictDataSource,
    private val regionDataSource: FirebaseRegionDataSource
) : ViewModel() {

    private val _district = MutableStateFlow<FirestoreDistrict?>(null)
    val district: StateFlow<FirestoreDistrict?> = _district

    private val _regions = MutableStateFlow<List<FirestoreRegion>>(emptyList())
    val regions: StateFlow<List<FirestoreRegion>> = _regions

    private val _updateResult = MutableStateFlow<Result<Unit>?>(null)
    val updateResult: StateFlow<Result<Unit>?> = _updateResult

    private val _deleteResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteResult: StateFlow<Result<Unit>?> = _deleteResult

    fun loadDistrict(regionId: String, districtId: String) {
        viewModelScope.launch {
            val result = districtDataSource.getDistrictById(regionId, districtId)
            if (result.isSuccess) {
                _district.value = result.getOrNull()
            } else {
                _district.value = null
            }
        }
    }


    fun updateDistrict(regionId: String, districtId: String, newName: String, regionName: String) {
        if (newName.isBlank()) {
            _updateResult.value = Result.failure(Exception("Tên quận/huyện không được để trống"))
            return
        }
        val updatedData = mapOf(
            "name" to newName,
            "regionName" to regionName
        )
        viewModelScope.launch {
            val result = districtDataSource.updateDistrict(regionId, districtId, updatedData)
            _updateResult.value = result
        }
    }

    fun deleteDistrict(regionId: String, districtId: String) {
        viewModelScope.launch {
            val result = districtDataSource.deleteDistrict(regionId, districtId)
            _deleteResult.value = result
        }
    }

    fun resetResults() {
        _updateResult.value = null
        _deleteResult.value = null
    }
}
