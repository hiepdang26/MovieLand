package com.example.admin.data.api

import com.example.admin.data.model.apimovie.NowPlayingMovieResponse
import com.example.admin.data.model.apimovie.UpcomingMovieResponse
import com.example.admin.data.model.apimovie.detail.DetailMovieResponse
import com.example.admin.data.model.apimovie.trailer.GetTrailerMovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MovieApi {

    @GET("3/movie/now_playing")
    suspend fun getAllNowPlayingMovie(
        @Header("Authorization") accessToken: String,
    ): Response<NowPlayingMovieResponse>

    @GET("3/movie/upcoming")
    suspend fun getAllUpcomingMovie(
        @Header("Authorization") accessToken: String,
    ): Response<UpcomingMovieResponse>


    @GET("3/movie/{movie_id}")
    suspend fun getDetailMovie(
        @Header("Authorization") accessToken: String,
        @Path("movie_id") movieId: Int,
    ): Response<DetailMovieResponse>

    @GET("3/movie/{movie_id}/videos")
    suspend fun getTrailerMovie(
        @Header("Authorization") accessToken: String,
        @Path("movie_id") movieId: Int,
    ): Response<GetTrailerMovieResponse>

}