package com.example.movieland

import android.app.Application

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieLandApplication : Application(){
    override fun onCreate() {
        super.onCreate()

    }
}