package com.example.admin.ui.features.mainmovie.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.cloudinary.CloudinaryUploader
import com.example.admin.data.firebase.datasource.FirebaseMovieDataSource
import com.example.admin.data.firebase.model.FirestoreMovie
import com.example.admin.domain.usecase.firebase.movie.UploadMovieToFirestoreUseCase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddRawMovieViewModel @Inject constructor(
    private val firebaseMovieDataSource: FirebaseMovieDataSource,
    private val cloudinaryUploader: CloudinaryUploader
) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<Unit>>()
    val uploadResult: LiveData<Result<Unit>> = _uploadResult

    private val _uploadedPosterUrl = MutableLiveData<String?>()
    val uploadedPosterUrl: LiveData<String?> = _uploadedPosterUrl

    fun uploadPosterAndMovie(uri: Uri, movie: FirestoreMovie) {
        viewModelScope.launch {
            val result = cloudinaryUploader.uploadImage(uri)
            result.onSuccess { url ->
                _uploadedPosterUrl.value = url
                val updatedMovie = movie.copy(posterPath = url)
                uploadMovie(updatedMovie)
            }.onFailure {
                _uploadResult.value = Result.failure(it)
            }
        }
    }

    private suspend fun uploadMovie(movie: FirestoreMovie) {
        _uploadResult.value = firebaseMovieDataSource.uploadMovie(movie)
    }
}


