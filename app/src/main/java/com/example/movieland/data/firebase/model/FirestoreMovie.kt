package com.example.movieland.data.firebase.model

import com.google.firebase.Timestamp
data class FirestoreMovie(
    val id: String = "",
    val title: String = "",
    val overview: String = "",
    val posterPath: String = "",
    val trailerKey: String = "", // đổi từ trailerUrl
    val runtime: Int = 0,
    val releaseDate: String = "",
    val genres: List<String> = emptyList(),
    val adult: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)

