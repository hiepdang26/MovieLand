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
                val availableCombos = combos.filter { it.available == true }
                _comboList.value = availableCombos
                _comboCounts.value = availableCombos.associate { it.id to 0 }
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

    fun resetTicketIfLockedByCurrentUser(showtimeId: String, ticketId: String, userId: String?) {
        if (userId == null) return
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("showtimes").document(showtimeId).collection("tickets").document(ticketId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val status = snapshot.getString("status")
            val currentUser = snapshot.getString("userId")
            if (status == "locked" && currentUser == userId) {
                Log.d("ChooseComboViewModel", "ticketId=$ticketId status=$status currentUser=$currentUser (expected=$userId)")
                transaction.update(docRef, mapOf(
                    "status" to "available",
                    "userId" to null,
                    "bookingTime" to null
                ))
            }else {
                Log.d("ChooseComboViewModel", "Không thoả điều kiện reset: status=$status, currentUser=$currentUser, expected=$userId")
            }
            null
        }.addOnSuccessListener {
            Log.d("ChooseComboViewModel", "Đã reset vé $ticketId nếu phù hợp")
        }.addOnFailureListener { e ->
            Log.e("ChooseComboViewModel", "Lỗi khi reset vé $ticketId: ${e.message}")
        }
    }



}
