package com.example.movieland.ui.features.home.combo

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.movieland.data.firebase.datasource.FirebaseComboDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.model.combo.FirestoreCombo
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChooseComboViewModel @Inject constructor(
    private val firebaseComboDataSource: FirebaseComboDataSource,
    private val firestore: FirebaseFirestore

) : ViewModel() {

    private val _comboList = MutableStateFlow<List<FirestoreCombo>>(emptyList())
    val comboList: StateFlow<List<FirestoreCombo>> = _comboList

    private val _comboCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val comboCounts: StateFlow<Map<String, Int>> = _comboCounts

    fun loadCombos(districtId: String) {
        viewModelScope.launch {
            val result = firebaseComboDataSource.loadCombosByDistrict(districtId)
            result.onSuccess { combos ->
                _comboList.value = combos
                _comboCounts.value = combos.associate { it.id to 0 }
            }
        }
    }

    fun increaseCount(comboId: String) {
        val current = _comboCounts.value[comboId] ?: 0
        val newMap = _comboCounts.value.toMutableMap()
        newMap[comboId] = current + 1
        _comboCounts.value = newMap
    }

    fun decreaseCount(comboId: String) {
        val current = _comboCounts.value[comboId] ?: 0
        if (current > 0) {
            val newMap = _comboCounts.value.toMutableMap()
            newMap[comboId] = current - 1
            _comboCounts.value = newMap
        }
    }

    fun updateTicketStatus(
        showtimeId: String,
        ticketId: String,
        status: String,
        userId: String? = null
    ) {
        val docRef = firestore
            .collection("showtimes")
            .document(showtimeId)
            .collection("tickets")
            .document(ticketId)

        val updateMap = mutableMapOf<String, Any>(
            "status" to status
        )

        if (userId != null) {
            updateMap["userId"] = userId
            updateMap["bookingTime"] = FieldValue.serverTimestamp()
        } else {
            updateMap["userId"] = FieldValue.delete()
            updateMap["bookingTime"] = FieldValue.delete()
        }

        docRef.update(updateMap)
            .addOnSuccessListener {
                Log.d("ChooseComboViewModel", "Updated ticket $ticketId to $status")
            }
            .addOnFailureListener { e ->
                Log.e("ChooseComboViewModel", "Failed to update ticket $ticketId", e)
            }
    }
}
