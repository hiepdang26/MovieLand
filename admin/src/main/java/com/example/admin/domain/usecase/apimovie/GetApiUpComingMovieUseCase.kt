package com.example.admin.domain.usecase.apimovie

import com.example.admin.data.model.apimovie.NowPlayingMovieResponse
import com.example.admin.data.model.apimovie.UpcomingMovieResponse
import com.example.admin.data.repository.network.movie.MovieRepository
import com.example.admin.data.resource.Result
import javax.inject.Inject

class GetApiUpComingMovieUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(accessToken: String):  Result<UpcomingMovieResponse> {
        return repository.getUpcomingMovie(accessToken)
    }
}