package com.example.admin.ui.features.showtimes.edit

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
import javax.inject.Inject

@HiltViewModel
class EditShowtimeViewModel @Inject constructor(
    private val showtimeDataSource: FirebaseShowtimeDataSource,
    private val movieDataSource: FirebaseMovieDataSource,
) : ViewModel() {

    private val _showtimeDetail = MutableStateFlow<FirestoreShowtime?>(null)
    val showtimeDetail: StateFlow<FirestoreShowtime?> = _showtimeDetail

    private val _updateResult = MutableStateFlow<Result<Unit>?>(null)
    val updateResult: StateFlow<Result<Unit>?> = _updateResult

    private val _deleteResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteResult: StateFlow<Result<Unit>?> = _deleteResult

    private val _movies = MutableStateFlow<List<FirestoreMovie>>(emptyList())
    val movies: StateFlow<List<FirestoreMovie>> = _movies

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

    fun loadShowtime(showtimeId: String) {
        viewModelScope.launch {
            val result = showtimeDataSource.getDetailShowtime(showtimeId)
            if (result.isSuccess) {
                _showtimeDetail.value = result.getOrNull()
            } else {
                _showtimeDetail.value = null
            }
        }
    }

    fun updateShowtime(showtimeId: String, updatedShowtime: FirestoreShowtime) {
        viewModelScope.launch {
            val mapData = updatedShowtime.toMap()
            val result = showtimeDataSource.updateShowtime(showtimeId, mapData)
            _updateResult.value = result
        }
    }

    fun deleteShowtime(showtimeId: String) {
        viewModelScope.launch {
            val result = showtimeDataSource.deleteShowtime(showtimeId)
            _deleteResult.value = result
        }
    }

    fun resetUpdateResult() {
        _updateResult.value = null
    }

    fun resetDeleteResult() {
        _deleteResult.value = null
    }

    fun FirestoreShowtime.toMap(): Map<String, Any?> {
        return mapOf(
            "roomId" to roomId,
            "movieId" to movieId,
            "movieName" to movieName,
            "startTime" to startTime,
            "endTime" to endTime,
            "date" to date,
            "availableSeats" to availableSeats,
            "bookedSeats" to bookedSeats,
            "price" to price,
            "status" to status,
            "screenType" to screenType,
            "screenCategory" to screenCategory,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

}
