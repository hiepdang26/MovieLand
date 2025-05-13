package com.example.admin.ui.features.apimovie.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseMovieDataSource
import com.example.admin.data.firebase.model.FirestoreMovie
import com.example.admin.data.model.apimovie.detail.DetailMovieResponse
import com.example.admin.data.model.apimovie.trailer.GetTrailerMovieResponse
import com.example.admin.domain.usecase.apimovie.GetDetailMovieUseCase
import com.example.admin.domain.usecase.apimovie.GetTrailerMovieUseCase
import com.example.admin.domain.usecase.firebase.movie.UploadMovieToFirestoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailMovieViewModel @Inject constructor(
    private val getDetailMovieUseCase: GetDetailMovieUseCase,
    private val getTrailerMovieUseCase: GetTrailerMovieUseCase,
    private val uploadMovieToFirestoreUseCase: UploadMovieToFirestoreUseCase

) : ViewModel() {
    private val _detailMovieResponse = MutableLiveData<DetailMovieResponse?>()
    val detailMovieResponse: LiveData<DetailMovieResponse?> = _detailMovieResponse
    private val _detailMovieResponseError = MutableLiveData<Exception?>()
    val detailMovieResponseError: LiveData<Exception?> = _detailMovieResponseError


    fun getDetailMovie(accessToken: String, movieId: Int) {
        viewModelScope.launch {
            try {
                val result = getDetailMovieUseCase(accessToken, movieId)
                if (result is com.example.admin.data.resource.Result.Success) {
                    _detailMovieResponse.value = result.data
                } else if (result is com.example.admin.data.resource.Result.Error) {
                    val errorMessage = result.exception.message ?: "Unknown error"
                    if (errorMessage.contains(
                            "Too many request and out limit",
                            ignoreCase = true
                        )
                    ) {
                        _detailMovieResponseError.value =
                            Exception("Too many request and out limit")
                    } else {
                        _detailMovieResponseError.value = result.exception
                    }
                }
            } catch (e: Exception) {
                _detailMovieResponseError.value = e
            }
        }
    }

    private val _trailerMovieResponse = MutableLiveData<GetTrailerMovieResponse?>()
    val trailerMovieResponse: LiveData<GetTrailerMovieResponse?> = _trailerMovieResponse
    private val _trailerMovieResponseError = MutableLiveData<Exception?>()
    val trailerMovieResponseError: LiveData<Exception?> = _trailerMovieResponseError


    fun getTrailerMovie(accessToken: String, movieId: Int) {
        viewModelScope.launch {
            try {
                val result = getTrailerMovieUseCase(accessToken, movieId)
                if (result is com.example.admin.data.resource.Result.Success) {
                    _trailerMovieResponse.value = result.data
                } else if (result is com.example.admin.data.resource.Result.Error) {
                    val errorMessage = result.exception.message ?: "Unknown error"
                    if (errorMessage.contains(
                            "Too many request and out limit",
                            ignoreCase = true
                        )
                    ) {
                        _trailerMovieResponseError.value =
                            Exception("Too many request and out limit")
                    } else {
                        _trailerMovieResponseError.value = result.exception
                    }
                }
            } catch (e: Exception) {
                _trailerMovieResponseError.value = e
            }
        }
    }


    private val _uploadResult = MutableLiveData<Result<Unit>>()
    val uploadResult: LiveData<Result<Unit>> = _uploadResult

    fun uploadMovie(movie: FirestoreMovie) {
        viewModelScope.launch {
            _uploadResult.value = uploadMovieToFirestoreUseCase(movie)
        }
    }
}