package com.example.movieland.ui.features.home.regionanddistrict

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseDistrictDataSource
import com.example.movieland.data.firebase.datasource.FirebaseRegionDataSource
import com.example.movieland.data.firebase.model.district.FirestoreDistrict
import com.example.movieland.data.firebase.model.region.FirestoreRegion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseRegionAndDistrictViewModel @Inject constructor(
    private val regionDataSource: FirebaseRegionDataSource,
    private val districtDataSource: FirebaseDistrictDataSource
) : ViewModel() {

    private val _regions = MutableStateFlow<List<FirestoreRegion>>(emptyList())
    val regions: StateFlow<List<FirestoreRegion>> = _regions

    private val _districts = MutableStateFlow<List<FirestoreDistrict>>(emptyList())
    val districts: StateFlow<List<FirestoreDistrict>> = _districts

    private val _selectedRegionId = MutableStateFlow<String?>(null)


    init {
        viewModelScope.launch {
            regionDataSource.getAllRegions().collect {
                _regions.value = it
            }
        }

        viewModelScope.launch {
            _selectedRegionId.flatMapLatest { regionId ->
                if (regionId != null) {
                    districtDataSource.getDistricts(regionId)
                } else {
                    kotlinx.coroutines.flow.flowOf(emptyList())
                }
            }.collect { districts ->
                _districts.value = districts
            }
        }
    }

    fun onRegionSelected(regionId: String) {
        _selectedRegionId.value = regionId
    }
}