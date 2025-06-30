package com.example.admin.ui.features.combo.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.cloudinary.CloudinaryUploader
import com.example.admin.data.firebase.datasource.FirebaseComboDataSource
import com.example.admin.data.firebase.model.combo.FirestoreCombo
import com.example.admin.data.firebase.model.district.FirestoreDistrict
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddCombosViewModel @Inject constructor(
    private val cloudinaryUploader: CloudinaryUploader,
    private val comboDataSource: FirebaseComboDataSource
) : ViewModel() {

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun clearError() {
        _error.value = null
    }

    fun uploadComboWithImage(
        imageUri: Uri,
        name: String,
        description: String,
        price: Double,
        districtId: String,
        districtName: String,
        available: Boolean
    ) {
        viewModelScope.launch {
            try {
                val result = cloudinaryUploader.uploadImage(imageUri)
                if (result.isFailure) throw result.exceptionOrNull() ?: Exception("Không thể upload ảnh")

                val imageUrl = result.getOrThrow()
                val comboId = "combo_" + System.currentTimeMillis()

                val combo = FirestoreCombo(
                    id = comboId,
                    name = name,
                    description = description,
                    imageUrl = imageUrl,
                    price = price,
                    districtId = districtId,
                    districtName = districtName,
                    available = available,
                    createdAt = Date(),
                    updatedAt = Date()
                )

                val response = comboDataSource.addCombo(combo)
                if (response.isSuccess) {
                    _success.value = true
                } else {
                    throw response.exceptionOrNull() ?: Exception("Không rõ lỗi khi thêm combo")
                }

            } catch (e: Exception) {
                _error.value = "❌ Lỗi khi thêm combo: ${e.message}"
            }
        }
    }
}

