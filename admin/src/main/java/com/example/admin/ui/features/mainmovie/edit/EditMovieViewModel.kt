package com.example.admin.ui.features.mainmovie.edit

import android.net.Uri
import androidx.lifecycle.*
import com.example.admin.data.cloudinary.CloudinaryUploader
import com.example.admin.data.firebase.datasource.FirebaseMovieDataSource
import com.example.admin.data.firebase.model.FirestoreMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMovieViewModel @Inject constructor(
    private val movieDataSource: FirebaseMovieDataSource,
    private val cloudinaryUploader: CloudinaryUploader
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

    fun updateMovie(
        movieId: String,
        updatedFields: Map<String, Any>,
        newPosterUri: Uri? = null
    ) {
        viewModelScope.launch {
            if (newPosterUri != null) {
                val uploadResult = cloudinaryUploader.uploadImage(newPosterUri)
                uploadResult.onSuccess { url ->
                    val finalFields = updatedFields.toMutableMap()
                    finalFields["posterPath"] = url
                    updateFirestore(movieId, finalFields)
                }.onFailure {
                    _updateResult.value = Result.failure(it)
                }
            } else {
                updateFirestore(movieId, updatedFields)
            }
        }
    }

    private suspend fun updateFirestore(movieId: String, data: Map<String, Any>) {
        _updateResult.value = movieDataSource.updateMovie(movieId, data)
    }

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    fun deleteMovie(movieId: String) {
        viewModelScope.launch {
            val result = movieDataSource.deleteMovie(movieId)
            _deleteResult.value = result
        }
    }
}
