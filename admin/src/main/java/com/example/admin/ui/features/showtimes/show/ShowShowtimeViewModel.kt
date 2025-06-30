package com.example.admin.ui.features.showtimes.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseShowtimeDataSource
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowShowtimeViewModel @Inject constructor(
    private val showtimeDataSource: FirebaseShowtimeDataSource
) : ViewModel() {

    private val _showtimes = MutableStateFlow<List<FirestoreShowtime>>(emptyList())
    val showtimes: StateFlow<List<FirestoreShowtime>> = _showtimes

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadShowtimesByRoom(roomId: String) {
        viewModelScope.launch {
            val result = showtimeDataSource.getAllShowtimesByRoomId(roomId)
            if (result.isSuccess) {
                val showtimeList = result.getOrDefault(emptyList())
                val sortedList = showtimeList.sortedByDescending { it.createdAt }
                _showtimes.value = sortedList
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Lỗi không xác định khi tải suất chiếu"
            }
        }
    }


    fun clearError() {
        _error.value = null
    }
}
