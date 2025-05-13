package com.example.admin.domain.usecase.firebase.movie

import com.example.admin.data.firebase.datasource.FirebaseMovieDataSource
import com.example.admin.data.firebase.model.FirestoreMovie
import javax.inject.Inject

class UploadMovieToFirestoreUseCase @Inject constructor(
    private val firebaseMovieDataSource: FirebaseMovieDataSource
) {
    suspend operator fun invoke(movie: FirestoreMovie): Result<Unit> {
        return firebaseMovieDataSource.uploadMovie(movie)
    }
}
