package com.example.movieland.di

import com.example.movieland.data.firebase.datasource.FirebaseAuthDataSource
import com.example.movieland.data.firebase.datasource.FirebaseMovieDataSource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseMovieDataSource(firestore: FirebaseFirestore): FirebaseMovieDataSource =
        FirebaseMovieDataSource(firestore)

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(firestore: FirebaseFirestore): FirebaseAuthDataSource =
        FirebaseAuthDataSource(firestore)
}
