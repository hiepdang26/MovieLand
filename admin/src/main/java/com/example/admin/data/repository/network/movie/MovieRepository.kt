package com.example.admin.data.repository.network.movie

import com.example.admin.data.model.apimovie.NowPlayingMovieResponse
import com.example.admin.data.model.apimovie.UpcomingMovieResponse
import com.example.admin.data.model.apimovie.detail.DetailMovieResponse
import com.example.admin.data.model.apimovie.trailer.GetTrailerMovieResponse

interface MovieRepository {
    suspend fun getNowPlayingMovie(accessToken: String): com.example.admin.data.resource.Result<NowPlayingMovieResponse>
    suspend fun getUpcomingMovie(accessToken: String): com.example.admin.data.resource.Result<UpcomingMovieResponse>
    suspend fun getDetailMovie(
        accessToken: String, movieId: Int
    ): com.example.admin.data.resource.Result<DetailMovieResponse>

    suspend fun getTrailerMovie(
        accessToken: String, movieId: Int
    ): com.example.admin.data.resource.Result<GetTrailerMovieResponse>

}