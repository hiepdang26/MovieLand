package com.example.admin.ui.features.showtimes.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseShowtimeDataSource
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddShowtimeViewModel @Inject constructor(
    private val showtimeDataSource: FirebaseShowtimeDataSource
) : ViewModel() {

    private val _saveResult = MutableStateFlow<Result<String>?>(null)  // Trả về Result<String>
    val saveResult: StateFlow<Result<String>?> = _saveResult

    fun addShowtime(
        roomId: String,
        movieId: String,
        movieName: String,
        startTime: Date,
        endTime: Date,
        date: Date,
        status: String
    ) {
        val newShowtime = FirestoreShowtime(
            roomId = roomId,
            movieId = movieId,
            movieName = movieName,
            startTime = startTime,
            endTime = endTime,
            date = date,
            availableSeats = 0,
            status = status,
            createdAt = Date(),
            updatedAt = Date()
        )

        viewModelScope.launch {
            val result = showtimeDataSource.addShowtime(newShowtime)
            _saveResult.value = result
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }
}

