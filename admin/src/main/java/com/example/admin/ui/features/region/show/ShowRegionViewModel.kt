package com.example.admin.ui.features.region.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseRegionDataSource
import com.example.admin.data.firebase.model.region.FirestoreRegion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowRegionViewModel @Inject constructor(
    private val regionDataSource: FirebaseRegionDataSource
) : ViewModel() {

    private val _regions = MutableStateFlow<List<FirestoreRegion>>(emptyList())
    val regions: StateFlow<List<FirestoreRegion>> = _regions

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchRegions()
    }

    private fun fetchRegions() {
        viewModelScope.launch {
            regionDataSource.getAllRegions()
                .onStart {
                    _errorMessage.value = null
                }
                .catch { e ->
                    _errorMessage.value = e.message
                    _regions.value = emptyList()
                }
                .collect { list ->
                    _regions.value = list
                }
        }
    }
}
