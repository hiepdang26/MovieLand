package com.example.movieland.ui.features.home.payment.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseMovieDataSource
import com.example.movieland.data.firebase.model.FirestoreMovie
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPaymentViewModel @Inject constructor(
    private val firebaseMovieDataSource: FirebaseMovieDataSource
) : ViewModel() {
    fun fetchTicketsByBookingId(bookingId: String, callback: (List<FirestoreTicket>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collectionGroup("tickets").whereEqualTo("bookingId", bookingId).get()
            .addOnSuccessListener { querySnapshot ->
                val tickets =
                    querySnapshot.documents.mapNotNull { it.toObject(FirestoreTicket::class.java) }
                tickets.forEach { ticket ->
                    Log.d("DetailPaymentVM", "Ticket: $ticket")
                }
                Log.d("DetailPaymentVM", "Tổng vé lấy được: ${tickets.size}")
                callback(tickets)
            }.addOnFailureListener { e ->
                Log.e("DetailPaymentVM", "Lỗi khi fetch tickets: ${e.message}", e)
            }
    }

    private val _movieDetail = MutableLiveData<FirestoreMovie>()
    val movieDetail: LiveData<FirestoreMovie> = _movieDetail

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadMovieDetail(movieId: String) {
        viewModelScope.launch {
            val result = firebaseMovieDataSource.getMovieById(movieId)
            result.onSuccess { _movieDetail.value = it }
                .onFailure { _updateResult.value = Result.failure(it) }
        }
    }
}
