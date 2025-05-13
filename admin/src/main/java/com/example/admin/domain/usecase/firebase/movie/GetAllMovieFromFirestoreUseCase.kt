package com.example.admin.domain.usecase.firebase.movie

import com.example.admin.data.firebase.datasource.FirebaseMovieDataSource
import com.example.admin.data.firebase.model.FirestoreMovie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMovieFromFirestoreUseCase @Inject constructor(
    private val firebaseMovieDataSource: FirebaseMovieDataSource
) {
    operator fun invoke(): Flow<List<FirestoreMovie>> {
        return firebaseMovieDataSource.getAllMovies()
    }
}