package com.example.movieland.ui.features.personal.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseAuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowPersonalViewModel @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource

) : ViewModel(){
    private val _userData = MutableLiveData<Map<String, Any>?>()
    val userData: LiveData<Map<String, Any>?> = _userData

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val data = authDataSource.getCurrentUserData()
                _userData.value = data
            } catch (e: Exception) {
                Log.e("PersonalInfoViewModel", "Lỗi loadCurrentUser: ${e.message}", e)
            }
        }
    }

    private val _changePasswordResult = MutableLiveData<Result<Unit>>()
    val changePasswordResult: LiveData<Result<Unit>> = _changePasswordResult

    fun changePassword(email: String, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            val result = authDataSource.changePassword(email, oldPassword, newPassword)
            _changePasswordResult.value = result
        }
    }


}