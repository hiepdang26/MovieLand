package com.example.admin.ui.features.room.add

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseRoomDataSource
import com.example.admin.data.firebase.model.room.FirestoreRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddRoomViewModel @Inject constructor(
    private val roomDataSource: FirebaseRoomDataSource
) : ViewModel() {

    private val _saveResult = MutableStateFlow<Result<Unit>?>(null)
    val saveResult: StateFlow<Result<Unit>?> = _saveResult

    fun saveRoom(districtId: String,districtName: String,name: String, totalSeat: Int,  seatInRow: Int, layoutJson: String) {
        if (name.isBlank()) {
            _saveResult.value = Result.failure(Exception("Tên phòng không được để trống"))
            return
        }
        if (districtId.isBlank()) {
            _saveResult.value = Result.failure(Exception("Phải chọn quận/huyện"))
            return
        }
        val now = Date()

        val room = FirestoreRoom(
            districtId = districtId,
            districtName = districtName,
            name = name,
            totalSeats = totalSeat,
            seatInRow = seatInRow,
            layoutJson = layoutJson,
            createdAt = now,
            updatedAt = now
        )

        viewModelScope.launch {
            val result = roomDataSource.addRoom(room)
            _saveResult.value = result
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }
}
