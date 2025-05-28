package com.example.admin.ui.features.showtimes.show.model

import com.example.admin.data.firebase.model.showtime.FirestoreShowtime

data class MovieWithShowtimes(
    val movieName: String,
    val showtimes: List<FirestoreShowtime>
)
