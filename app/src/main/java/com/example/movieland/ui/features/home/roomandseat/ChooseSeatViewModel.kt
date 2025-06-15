package com.example.movieland.ui.features.home.roomandseat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseRoomDataSource
import com.example.movieland.data.firebase.model.room.FirestoreRoom
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseSeatViewModel @Inject constructor(
    private val roomDataSource: FirebaseRoomDataSource
) : ViewModel() {

    private val _room = MutableStateFlow<FirestoreRoom?>(null)
    val room: StateFlow<FirestoreRoom?> = _room

    fun loadRoomDetail(roomId: String) {
        viewModelScope.launch {
            val result = roomDataSource.getDetailRoom(roomId)
            result.onSuccess { _room.value = it }
                .onFailure { Log.e("ChooseSeatViewModel", "Lỗi: ${it.message}") }
        }
    }

    fun loadTickets(showtimeId: String, onDone: (List<FirestoreTicket>) -> Unit) {
        viewModelScope.launch {
            val result = roomDataSource.getTicketsByShowtime(showtimeId)
            result.onSuccess { onDone(it) }
                .onFailure { Log.e("ChooseSeatViewModel", "Lỗi tickets: ${it.message}") }
        }
    }
}
