package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseRoomDataSource
import com.example.admin.data.firebase.model.room.FirestoreRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseRoomForTicketViewModel @Inject constructor(
    private val roomDataSource: FirebaseRoomDataSource
) : ViewModel() {

    private val _rooms = MutableStateFlow<List<FirestoreRoom>>(emptyList())
    val rooms: StateFlow<List<FirestoreRoom>> = _rooms

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadRoomsByDistrictId(districtId: String) {
        viewModelScope.launch {
            try {
                roomDataSource.getRoomsByDistrictId(districtId).collect { roomList ->
                    val sortedRooms =
                        roomList.sortedByDescending { it.createdAt ?: java.util.Date(0) }
                    _rooms.value = sortedRooms
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Lỗi khi tải danh sách phòng"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
