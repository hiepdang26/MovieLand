package com.example.admin.di

import android.content.Context
import com.example.admin.BuildConfig
import com.example.admin.data.api.MovieApi
import com.example.admin.data.cloudinary.CloudinaryUploader
import com.example.admin.data.firebase.datasource.FirebaseDistrictDataSource
import com.example.admin.data.firebase.datasource.FirebaseMovieDataSource
import com.example.admin.data.repository.network.movie.MovieRepositoryImp
import com.example.admin.data.repository.network.movie.MovieRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieApi(client: OkHttpClient): MovieApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApi::class.java)
    }


    @Provides
    @Singleton
    @Named("Movie")
    fun provideNamedMovieApi(movieApi: MovieApi): MovieApi = movieApi

    @Provides
    @Singleton
    fun provideMovieRepository(
        @Named("Movie") movieApi: MovieApi
    ): MovieRepository {
        return MovieRepositoryImp(movieApi)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseMovieDataSource(
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): FirebaseMovieDataSource = FirebaseMovieDataSource(firestore, context)


    @Provides
    @Singleton
    fun provideFirebaseDistrictDataSource(
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context

    ): FirebaseDistrictDataSource {
        return FirebaseDistrictDataSource(firestore, context)
    }

    @Provides
    @Singleton
    fun provideCloudinaryUploader(@ApplicationContext context: Context): CloudinaryUploader {
        return CloudinaryUploader(
            context = context,
            cloudName = "dhk8ckbfd",
            uploadPreset = "ml_default"
        )
    }
}
