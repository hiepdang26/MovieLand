package com.example.movieland.ui.features.home.movie.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.model.FirestoreMovie
import com.example.movieland.domain.usecase.GetAllMovieFromFirestoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ShowMovieViewModel @Inject constructor(
    private val getAllMovieFromFirestoreUseCase: GetAllMovieFromFirestoreUseCase
) : ViewModel() {

    private var allMovies: List<FirestoreMovie> = emptyList()

    private val _movies = MutableStateFlow<List<FirestoreMovie>>(emptyList())
    val movies: StateFlow<List<FirestoreMovie>> = _movies

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                getAllMovieFromFirestoreUseCase().collect { movieList ->
                    allMovies = movieList
                    _movies.value = movieList
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Lỗi không xác định khi fetch movies"
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _movies.value = allMovies
        } else {
            val filtered = allMovies.filter {
                it.title?.contains(query, ignoreCase = true) == true
            }
            _movies.value = filtered
        }
    }
}

