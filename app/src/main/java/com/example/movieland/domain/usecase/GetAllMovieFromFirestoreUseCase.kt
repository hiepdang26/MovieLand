package com.example.movieland.domain.usecase

import com.example.movieland.data.firebase.datasource.FirebaseMovieDataSource
import com.example.movieland.data.firebase.model.FirestoreMovie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMovieFromFirestoreUseCase @Inject constructor(
    private val firebaseMovieDataSource: FirebaseMovieDataSource
) {
    operator fun invoke(): Flow<List<FirestoreMovie>> {
        return firebaseMovieDataSource.getAllMovies()
    }
}