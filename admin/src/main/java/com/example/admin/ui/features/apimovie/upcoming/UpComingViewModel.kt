package com.example.admin.ui.features.apimovie.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.model.apimovie.UpcomingMovieResponse
import com.example.admin.data.resource.Result
import com.example.admin.domain.usecase.apimovie.GetApiNowPlayingMovieUseCase
import com.example.admin.domain.usecase.apimovie.GetApiUpComingMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class UpComingViewModel  @Inject constructor(
    private val getApiUpComingMovieUseCase: GetApiUpComingMovieUseCase,
    ): ViewModel() {

    private val _upComingMovieResponse = MutableLiveData<UpcomingMovieResponse?>()
    val upComingMovieResponse: LiveData<UpcomingMovieResponse?> = _upComingMovieResponse
    private val _upComingMovieResponseError = MutableLiveData<Exception?>()
    val upComingMovieResponseError: LiveData<Exception?> = _upComingMovieResponseError

    fun getUpComingMovie(accessToken: String) {
        viewModelScope.launch {
            try {
                val result = getApiUpComingMovieUseCase(accessToken)
                if (result is com.example.admin.data.resource.Result.Success) {
                    _upComingMovieResponse.value = result.data
                } else if (result is Result.Error) {
                    val errorMessage = result.exception.message ?: "Unknown error"
                    if (errorMessage.contains("Too many request and out limit", ignoreCase = true)) {
                        _upComingMovieResponseError.value = Exception("Too many request and out limit")
                    } else {
                        _upComingMovieResponseError.value = result.exception
                    }
                }
            } catch (e: Exception){
                _upComingMovieResponseError.value = e
            }
        }
    }
}