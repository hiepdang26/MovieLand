package com.example.admin.ui.features.ticket.show
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseTicketDataSource
import com.example.admin.data.firebase.model.ticket.FirestoreTicket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowTicketViewModel @Inject constructor(
    private val ticketDataSource: FirebaseTicketDataSource
) : ViewModel() {

    private val _tickets = MutableStateFlow<List<FirestoreTicket>>(emptyList())
    val tickets: StateFlow<List<FirestoreTicket>> = _tickets

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadTickets(showtimeId: String) {
        viewModelScope.launch {
            val result = ticketDataSource.getTickets(showtimeId)
            result.fold(
                onSuccess = { _tickets.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }
}
