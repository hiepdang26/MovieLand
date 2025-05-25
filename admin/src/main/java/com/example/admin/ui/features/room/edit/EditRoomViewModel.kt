package com.example.admin.ui.features.room.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseRoomDataSource
import com.example.admin.data.firebase.model.room.FirestoreRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Date
@HiltViewModel
class EditRoomViewModel @Inject constructor(
    private val roomDataSource: FirebaseRoomDataSource
) : ViewModel() {

    private val _roomDetail = MutableStateFlow<FirestoreRoom?>(null)
    val roomDetail: StateFlow<FirestoreRoom?> = _roomDetail

    private val _saveResult = MutableStateFlow<Result<Unit>?>(null)
    val saveResult: StateFlow<Result<Unit>?> = _saveResult

    private val _deleteResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteResult: StateFlow<Result<Unit>?> = _deleteResult

    fun loadRoom(roomId: String) {
        viewModelScope.launch {
            val result = roomDataSource.getDetailRoom(roomId)
            if (result.isSuccess) {
                _roomDetail.value = result.getOrNull()
            } else {
                _roomDetail.value = null
            }
        }
    }

    fun saveRoom(
        roomId: String, name: String, totalSeat: Int, seatInEachRow: Int, layoutJson: String
    ) {
        viewModelScope.launch {
            val currentRoom = _roomDetail.value
            if (currentRoom == null) {
                _saveResult.value = Result.failure(Exception("Phòng chưa được tải"))
                return@launch
            }

            val updatedRoom = currentRoom.copy(
                name = name,
                totalSeats = totalSeat,
                seatInRow = seatInEachRow,
                layoutJson = layoutJson,
                updatedAt = Date()
            )
            val result = roomDataSource.editRoom(roomId, updatedRoom.toMap())
            _saveResult.value = result
        }
    }

    fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            val result = roomDataSource.deleteRoom(roomId)
            _deleteResult.value = result
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }

    fun resetDeleteResult() {
        _deleteResult.value = null
    }
}

