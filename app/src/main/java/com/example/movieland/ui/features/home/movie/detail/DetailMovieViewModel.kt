package com.example.movieland.ui.features.home.movie.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.movieland.data.firebase.datasource.FirebaseMovieDataSource
import com.example.movieland.data.firebase.model.FirestoreMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailMovieViewModel  @Inject constructor(
    private val movieDataSource: FirebaseMovieDataSource
) : ViewModel() {

    private val _movieDetail = MutableLiveData<FirestoreMovie>()
    val movieDetail: LiveData<FirestoreMovie> = _movieDetail

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadMovieDetail(movieId: String) {
        viewModelScope.launch {
            val result = movieDataSource.getMovieById(movieId)
            result.onSuccess { _movieDetail.value = it }
                .onFailure { _updateResult.value = Result.failure(it) }
        }
    }
}