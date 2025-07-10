package com.example.admin.ui.features.district.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseDistrictDataSource
import com.example.admin.data.firebase.datasource.FirebaseRegionDataSource
import com.example.admin.data.firebase.model.district.FirestoreDistrict
import com.example.admin.data.firebase.model.region.FirestoreRegion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDistrictViewModel @Inject constructor(
    private val districtDataSource: FirebaseDistrictDataSource
) : ViewModel() {
    private val _addResult = MutableStateFlow<Result<Unit>?>(null)
    val addResult: StateFlow<Result<Unit>?> = _addResult.asStateFlow()

    fun addDistrict(regionId: String, regionName: String, districtName: String) {
        if (regionId.isBlank() || districtName.isBlank()) {
            _addResult.value = Result.failure(Exception("Vui lòng chọn khu vực và nhập tên quận/huyện"))
            return
        }

        val newDistrict = FirestoreDistrict(
            id = "",
            name = districtName,
            regionId = regionId,
            regionName = regionName
        )

        viewModelScope.launch {
            val result = districtDataSource.uploadDistrict(regionId, newDistrict)
            _addResult.value = result
        }
    }


    fun resetAddResult() {
        _addResult.value = null
    }
}
