package com.example.movieland.ui.features.home.showtime

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseShowtimeDataSource
import com.example.movieland.data.firebase.model.showtime.FirestoreShowtime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ShowShowtimeViewModel @Inject constructor(
    private val showtimeDataSource: FirebaseShowtimeDataSource
) : ViewModel() {

    private val _showtimes = MutableStateFlow<List<FirestoreShowtime>>(emptyList())
    val showtimes: StateFlow<List<FirestoreShowtime>> = _showtimes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _showtimes2D = MutableStateFlow<List<FirestoreShowtime>>(emptyList())
    val showtimes2D: StateFlow<List<FirestoreShowtime>> = _showtimes2D

    private val _showtimes3D = MutableStateFlow<List<FirestoreShowtime>>(emptyList())
    val showtimes3D: StateFlow<List<FirestoreShowtime>> = _showtimes3D

    fun loadShowtimes(districtId: String, movieId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = showtimeDataSource.getShowtimesByDistrictAndMovie(districtId, movieId)
            _isLoading.value = false
            result.onSuccess { list ->
                _showtimes.value = list.filter { it.status.equals("active", ignoreCase = true) }
            }.onFailure {
                Log.e("ShowShowtimeVM", "Lỗi load showtimes: ${it.message}", it)
            }
        }
    }


    private val _filteredShowtimes = MutableStateFlow<List<FirestoreShowtime>>(emptyList())
    val filteredShowtimes: StateFlow<List<FirestoreShowtime>> = _filteredShowtimes

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterShowtimesByDate(date: LocalDate) {
        val filtered = _showtimes.value.filter { showtime ->
            showtime.date?.toInstant()?.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))?.toLocalDate() == date
        }

        _filteredShowtimes.value = filtered

        _showtimes2D.value = filtered.filter { it.screenType.equals("2D", ignoreCase = true) }
        _showtimes3D.value = filtered.filter { it.screenType.equals("3D", ignoreCase = true) }
    }

}
