package com.example.movieland.di

import android.content.Context
import com.example.movieland.data.firebase.datasource.FirebaseAuthDataSource
import com.example.movieland.data.firebase.datasource.FirebaseMovieDataSource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideFirebaseMovieDataSource(
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): FirebaseMovieDataSource = FirebaseMovieDataSource(firestore, context)

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(firestore: FirebaseFirestore): FirebaseAuthDataSource =
        FirebaseAuthDataSource(firestore)
}
