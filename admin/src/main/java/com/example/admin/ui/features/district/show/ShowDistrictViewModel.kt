package com.example.admin.ui.features.district.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseDistrictDataSource
import com.example.admin.data.firebase.model.district.FirestoreDistrict
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowDistrictViewModel @Inject constructor(
    private val districtDataSource: FirebaseDistrictDataSource
) : ViewModel() {

    private val _districts = MutableStateFlow<List<FirestoreDistrict>>(emptyList())
    val districts: StateFlow<List<FirestoreDistrict>> = _districts

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadDistricts(regionId: String) {
        viewModelScope.launch {
            districtDataSource.getDistricts(regionId)
                .onStart {
                    _errorMessage.value = null
                }
                .catch { e ->
                    _errorMessage.value = e.message
                    _districts.value = emptyList()
                }
                .collect { list ->
                    _districts.value = list
                }
        }
    }
}
