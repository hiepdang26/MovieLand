package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.district

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseDistrictDataSource
import com.example.admin.data.firebase.model.district.FirestoreDistrict
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChooseDistrictForTicketViewModel  @Inject constructor(
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