package com.example.admin.data.repository.network.movie

import android.util.Log
import com.example.admin.data.api.MovieApi
import com.example.admin.data.model.apimovie.NowPlayingMovieResponse
import com.example.admin.data.model.apimovie.UpcomingMovieResponse
import com.example.admin.data.model.apimovie.detail.DetailMovieResponse
import com.example.admin.data.model.apimovie.trailer.GetTrailerMovieResponse
import com.example.admin.data.resource.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class MovieRepositoryImp  @Inject constructor(
    @Named("Movie") private val movieApi: MovieApi
): MovieRepository {


    override suspend fun getNowPlayingMovie(accessToken: String): com.example.admin.data.resource.Result<NowPlayingMovieResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApi.getAllNowPlayingMovie(accessToken)
                if (response.isSuccessful) {
                    Log.d("GetNowPlayingMovieRepoImp", "Get NowPlayingMovie Successfully ")
                    com.example.admin.data.resource.Result.Success(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        Log.d("GetNowPlayingMovieRepoImp", "Unauthorized: ${response.message()}")
                    } else {
                        Log.d(
                            "GetNowPlayingMovieRepoImp",
                            "Get NowPlaying Failed: ${response.message()}"
                        )
                    }
                    com.example.admin.data.resource.Result.Error(Exception(response.message()))

                }
            } catch (e: Exception) {
                Log.e("GetNowPlayingMovieRepoImp", "Error occurred: ${e.message}", e)
                com.example.admin.data.resource.Result.Error(e)
            }
        }
    }

    override suspend fun getUpcomingMovie(accessToken: String): Result<UpcomingMovieResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApi.getAllUpcomingMovie(accessToken)
                if (response.isSuccessful) {
                    Log.d("GetUpcomingMovieRepoImp", "Get UpcomingMovie Successfully ")
                    com.example.admin.data.resource.Result.Success(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        Log.d("GetUpcomingMovieRepoImp", "Unauthorized: ${response.message()}")
                    } else {
                        Log.d(
                            "GetUpcomingMovieRepoImp", "Get Upcoming Failed: ${response.message()}"
                        )
                    }
                    com.example.admin.data.resource.Result.Error(Exception(response.message()))

                }
            } catch (e: Exception) {
                Log.e("GetUpcomingMovieRepoImp", "Error occurred: ${e.message}", e)
                com.example.admin.data.resource.Result.Error(e)
            }
        }
    }

    override suspend fun getDetailMovie(
        accessToken: String,
        movieId: Int
    ): Result<DetailMovieResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApi.getDetailMovie(accessToken, movieId)
                if (response.isSuccessful) {
                    Log.d("GetDetailMovieRepoImp", "Get UpcomingMovie Successfully ")
                    com.example.admin.data.resource.Result.Success(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        Log.d("GetDetailMovieRepoImp", "Unauthorized: ${response.message()}")
                    } else {
                        Log.d(
                            "GetDetailMovieRepoImp", "Get Upcoming Failed: ${response.message()}"
                        )
                    }
                    com.example.admin.data.resource.Result.Error(Exception(response.message()))

                }
            } catch (e: Exception) {
                Log.e("GetDetailMovieRepoImp", "Error occurred: ${e.message}", e)
                com.example.admin.data.resource.Result.Error(e)
            }
        }
    }

    override suspend fun getTrailerMovie(
        accessToken: String,
        movieId: Int
    ): Result<GetTrailerMovieResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApi.getTrailerMovie(accessToken, movieId)
                if (response.isSuccessful) {
                    Log.d("GetTrailerMovieRepoImp", "Get UpcomingMovie Successfully ")
                    com.example.admin.data.resource.Result.Success(response.body()!!)
                } else {
                    if (response.code() == 401) {
                        Log.d("GetTrailerMovieRepoImp", "Unauthorized: ${response.message()}")
                    } else {
                        Log.d(
                            "GetTrailerMovieRepoImp", "Get Upcoming Failed: ${response.message()}"
                        )
                    }
                    com.example.admin.data.resource.Result.Error(Exception(response.message()))

                }
            } catch (e: Exception) {
                Log.e("GetTrailerMovieRepoImp", "Error occurred: ${e.message}", e)
                com.example.admin.data.resource.Result.Error(e)
            }
        }
    }
}