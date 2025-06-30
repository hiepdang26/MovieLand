package com.example.movieland.ui.features.personal.ticket.show

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseTicketDataSource
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowBookedTicketsViewModel @Inject constructor(private val ticketDataSource: FirebaseTicketDataSource) :
    ViewModel() {
    private val _tickets = MutableLiveData<List<FirestoreTicket>>()
    val tickets: LiveData<List<FirestoreTicket>> = _tickets

    private val _filteredTickets = MutableLiveData<List<FirestoreTicket>>()
    val filteredTickets: LiveData<List<FirestoreTicket>> = _filteredTickets

    fun loadBookedTickets(userId: String) {
        viewModelScope.launch {
            try {
                val result = ticketDataSource.getBookedTicketsForUser(userId)
                if (result.isSuccess) {
                    val sortedList = (result.getOrNull() ?: emptyList())
                        .sortedByDescending { it.bookingTime?.time ?: 0L }
                    _tickets.value = sortedList
                    _filteredTickets.value = sortedList
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                    Log.e("ShowBookedTicketsVM", "Error getBookedTicketsForUser: $errorMsg")
                }
            } catch (e: Exception) {
                Log.e("ShowBookedTicketsVM", "Exception getBookedTicketsForUser: ${e.message}", e)
            }
        }
    }


    fun filterTicketsByMovieName(query: String) {
        val current = _tickets.value ?: emptyList()
        if (query.isBlank()) {
            _filteredTickets.value = current
        } else {
            _filteredTickets.value = current.filter {
                it.movieName.contains(query, ignoreCase = true)
            }
        }
    }

}