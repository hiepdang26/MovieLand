package com.example.admin.domain.usecase.apimovie

import com.example.admin.data.model.apimovie.detail.DetailMovieResponse
import com.example.admin.data.repository.network.movie.MovieRepository
import com.example.admin.data.resource.Result
import javax.inject.Inject

class GetDetailMovieUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(accessToken: String, movieId: Int):  Result<DetailMovieResponse> {
        return repository.getDetailMovie(accessToken, movieId)
    }
}