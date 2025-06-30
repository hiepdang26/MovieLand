package com.example.admin.ui.features.showtimes.add

import android.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseMovieDataSource
import com.example.admin.data.firebase.datasource.FirebaseShowtimeDataSource
import com.example.admin.data.firebase.model.FirestoreMovie
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddShowtimeViewModel @Inject constructor(
    private val showtimeDataSource: FirebaseShowtimeDataSource,
    private val movieDataSource: FirebaseMovieDataSource,

) : ViewModel() {

    private val _movies = MutableStateFlow<List<FirestoreMovie>>(emptyList())
    val movies: StateFlow<List<FirestoreMovie>> = _movies

    private val _saveResult = MutableStateFlow<Result<String>?>(null)
    val saveResult: StateFlow<Result<String>?> = _saveResult

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            movieDataSource.getAllMovies().collectLatest { movieList ->
                _movies.value = movieList
            }
        }
    }

    fun addShowtime(
        districtId: String,
        districtName: String,
        roomId: String,
        roomName: String,
        movieId: String,
        movieName: String,
        totalSeat : Int,
        seatInEachRow: Int,
        startTime: Date,
        endTime: Date,
        date: Date,
        status: String,
        screenType: String,
        screenCategory: String,
        price: Double,
    ) {
        val newShowtime = FirestoreShowtime(
            roomId = roomId,
            movieId = movieId,
            movieName = movieName,
            startTime = startTime,
            endTime = endTime,
            date = date,
            availableSeats = totalSeat,
            status = status,
            screenType = screenType,
            screenCategory = screenCategory,
            price = price,
            createdAt = Date(),
            updatedAt = Date(),
            districtId = districtId,
            districtName = districtName,
            roomName = roomName,
            totalSeat = totalSeat,
            seatInEachRow = seatInEachRow,
        )

        viewModelScope.launch {
            val result = showtimeDataSource.addShowtime(newShowtime)
            _saveResult.value = result
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }
    suspend fun getShowtimesByRoomAndDate(roomId: String, date: Date): List<FirestoreShowtime> {
        return showtimeDataSource.getShowtimesByRoomAndDate(roomId, date)
    }
}

