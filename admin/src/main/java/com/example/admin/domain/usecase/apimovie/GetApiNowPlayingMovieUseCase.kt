package com.example.admin.domain.usecase.apimovie

import com.example.admin.data.model.apimovie.NowPlayingMovieResponse
import com.example.admin.data.repository.network.movie.MovieRepository
import javax.inject.Inject

class GetApiNowPlayingMovieUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(accessToken: String):  com.example.admin.data.resource.Result<NowPlayingMovieResponse> {
        return repository.getNowPlayingMovie(accessToken)
    }
}