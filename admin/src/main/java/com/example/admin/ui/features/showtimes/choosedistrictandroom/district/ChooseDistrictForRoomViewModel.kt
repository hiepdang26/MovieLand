package com.example.admin.ui.features.showtimes.choosedistrictandroom.district

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.model.district.FirestoreDistrict
import com.example.admin.data.firebase.datasource.FirebaseDistrictDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseDistrictForRoomViewModel @Inject constructor(
    private val districtDataSource: FirebaseDistrictDataSource
) : ViewModel() {

    private val _districts = MutableStateFlow<List<FirestoreDistrict>>(emptyList())
    val districts: StateFlow<List<FirestoreDistrict>> = _districts

    fun loadDistricts() {
        viewModelScope.launch {
            districtDataSource.getAllDistricts()
                .collect { list ->
                    _districts.value = list
                }
        }
    }
}

