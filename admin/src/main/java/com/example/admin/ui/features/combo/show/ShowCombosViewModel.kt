package com.example.admin.ui.features.combo.show

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseComboDataSource
import com.example.admin.data.firebase.model.combo.FirestoreCombo
import com.example.admin.data.firebase.model.district.FirestoreDistrict
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ShowCombosViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val comboDataSource: FirebaseComboDataSource
) : ViewModel() {

    private val _districts = MutableStateFlow<List<FirestoreDistrict>>(emptyList())
    val districts: StateFlow<List<FirestoreDistrict>> = _districts

    private val _combos = MutableStateFlow<List<FirestoreCombo>>(emptyList())
    val combos: StateFlow<List<FirestoreCombo>> = _combos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun clearError() {
        _error.value = null
    }

    fun loadDistricts() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collectionGroup("districts").get().await()
                val list = snapshot.toObjects(FirestoreDistrict::class.java)

                Log.d("ShowCombosViewModel", "✅ Firestore trả về ${list.size} districts (via collectionGroup)")
                list.forEach {
                    Log.d("ShowCombosViewModel", "📍 District: ${it.id} - ${it.name}")
                }

                _districts.value = list
            } catch (e: Exception) {
                Log.e("ShowCombosViewModel", "❌ Lỗi loadDistricts: ${e.message}", e)
                _error.value = "Lỗi khi tải danh sách quận"
            }
        }
    }

    fun loadCombosByDistrict(districtId: String) {
        viewModelScope.launch {
            val result = comboDataSource.loadCombosByDistrict(districtId)
            result.onSuccess {
                _combos.value = it
            }.onFailure {
                _error.value = "Không thể tải combo: ${it.message}"
            }
        }
    }
}
