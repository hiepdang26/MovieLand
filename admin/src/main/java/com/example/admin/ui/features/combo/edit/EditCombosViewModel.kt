package com.example.admin.ui.features.combo.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseComboDataSource
import com.example.admin.data.firebase.model.combo.FirestoreCombo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCombosViewModel @Inject constructor(
    private val comboDataSource: FirebaseComboDataSource,
    private val cloudinaryUploader: com.example.admin.data.cloudinary.CloudinaryUploader
) : ViewModel() {

    private val _combo = MutableStateFlow<FirestoreCombo?>(null)
    val combo: StateFlow<FirestoreCombo?> = _combo.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _updateSuccess = MutableSharedFlow<Boolean>()
    val updateSuccess: SharedFlow<Boolean> = _updateSuccess.asSharedFlow()

    private val _deleteSuccess = MutableSharedFlow<Boolean>()
    val deleteSuccess: SharedFlow<Boolean> = _deleteSuccess.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun loadCombo(comboId: String) {
        viewModelScope.launch {
            val result = comboDataSource.getComboById(comboId)
            result.onSuccess {
                _combo.value = it
            }.onFailure {
                _error.emit("Không thể tải combo: ${it.message}")
            }
        }
    }

    fun updateCombo(comboId: String, updatedFields: Map<String, Any?>) {
        viewModelScope.launch {
            _isUpdating.value = true
            val result = comboDataSource.updateCombo(comboId, updatedFields)
            _isUpdating.value = false

            if (result.isSuccess) {
                _updateSuccess.emit(true)
            } else {
                _error.emit("Cập nhật thất bại: ${result.exceptionOrNull()?.message}")
            }
        }
    }
    fun deleteCombo(comboId: String) {
        viewModelScope.launch {
            val result = comboDataSource.deleteCombo(comboId)
            if (result.isSuccess) {
                _deleteSuccess.emit(true)
            } else {
                _error.emit("Xoá thất bại: ${result.exceptionOrNull()?.message}")
            }
        }
    }


    suspend fun uploadImageToCloudinary(uri: Uri): Result<String> {
        return cloudinaryUploader.uploadImage(uri)
    }
}

