package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.showtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ChooseShowtimeForTicketViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _showtimes = MutableStateFlow<List<FirestoreShowtime>>(emptyList())
    val showtimes: StateFlow<List<FirestoreShowtime>> = _showtimes

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadShowtimesByRoomId(roomId: String) {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("showtimes")
                    .whereEqualTo("roomId", roomId)
                    .get()
                    .await()

                val showtimeList = snapshot.toObjects(FirestoreShowtime::class.java)

                if (showtimeList.isEmpty()) {
                    _error.value = "Không có suất chiếu nào trong phòng này."
                } else {
                    _showtimes.value = showtimeList
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }



    fun clearError() {
        _error.value = null
    }
}
