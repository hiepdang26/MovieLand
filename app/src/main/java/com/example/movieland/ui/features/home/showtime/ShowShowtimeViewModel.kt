package com.example.movieland.ui.features.home.showtime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseShowtimeDataSource
import com.example.movieland.data.firebase.model.showtime.FirestoreShowtime
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadShowtimes(districtId: String, movieId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = showtimeDataSource.getShowtimesByDistrictAndMovie(districtId, movieId)
            _isLoading.value = false
            result.onSuccess { list ->
                _showtimes.value = list
            }.onFailure {
                Log.e("ShowShowtimeVM", "Lỗi load showtimes: ${it.message}", it)
            }
        }
    }
}
