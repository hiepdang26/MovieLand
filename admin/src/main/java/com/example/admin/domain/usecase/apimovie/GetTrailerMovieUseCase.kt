package com.example.admin.domain.usecase.apimovie

import com.example.admin.data.model.apimovie.detail.DetailMovieResponse
import com.example.admin.data.model.apimovie.trailer.GetTrailerMovieResponse
import com.example.admin.data.repository.network.movie.MovieRepository
import com.example.admin.data.resource.Result
import javax.inject.Inject

class GetTrailerMovieUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(accessToken: String, movieId: Int):  Result<GetTrailerMovieResponse> {
        return repository.getTrailerMovie(accessToken, movieId)
    }
}