package com.example.admin.ui.features.apimovie.nowplaying

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.model.apimovie.NowPlayingMovieResponse
import com.example.admin.data.model.apimovie.UpcomingMovieResponse
import com.example.admin.data.resource.Result
import com.example.admin.domain.usecase.apimovie.GetApiNowPlayingMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val getApiNowPlayingMovieUseCase: GetApiNowPlayingMovieUseCase
): ViewModel() {

    private val _nowPlayingMovieResponse = MutableLiveData<NowPlayingMovieResponse?>()
    val nowPlayingMovieResponse: LiveData<NowPlayingMovieResponse?> = _nowPlayingMovieResponse
    private val _nowPlayingMovieResponseError = MutableLiveData<Exception?>()
    val nowPlayingMovieResponseError: LiveData<Exception?> = _nowPlayingMovieResponseError


    fun getNowPlayingMovie(accessToken: String) {
        viewModelScope.launch {
            try {
                val result = getApiNowPlayingMovieUseCase(accessToken)
                if (result is com.example.admin.data.resource.Result.Success) {
                    _nowPlayingMovieResponse.value = result.data
                } else if (result is Result.Error) {
                    val errorMessage = result.exception.message ?: "Unknown error"
                    if (errorMessage.contains(
                            "Too many request and out limit",
                            ignoreCase = true
                        )
                    ) {
                        _nowPlayingMovieResponseError.value =
                            Exception("Too many request and out limit")
                    } else {
                        _nowPlayingMovieResponseError.value = result.exception
                    }
                }
            } catch (e: Exception) {
                _nowPlayingMovieResponseError.value = e
            }
        }
    }
}

