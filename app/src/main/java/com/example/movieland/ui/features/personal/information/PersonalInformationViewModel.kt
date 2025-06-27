package com.example.movieland.ui.features.personal.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseAuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalInformationViewModel @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : ViewModel() {
    private val _userData = MutableLiveData<Map<String, Any>?>()
    val userData: LiveData<Map<String, Any>?> = _userData

    private val _updateResult = MutableLiveData<Result<Boolean>>()
    val updateResult: LiveData<Result<Boolean>> = _updateResult

    fun loadCurrentUser() {
        viewModelScope.launch {
            val data = firebaseAuthDataSource.getCurrentUserData()
            _userData.value = data
        }
    }

    fun updateUser(
        name: String,
        gender: String,
        birthdate: String,
        phone: String,
    ) {
        viewModelScope.launch {
            val result = firebaseAuthDataSource.updateCurrentUser(
                name, gender, birthdate, phone
            )
            _updateResult.value = result
        }
    }

    private val _deleteAccountResult = MutableLiveData<Result<Unit>>()
    val deleteAccountResult: LiveData<Result<Unit>> = _deleteAccountResult

    fun deleteAccountWithReauth(email: String, password: String) {
        viewModelScope.launch {
            val result = firebaseAuthDataSource.deleteCurrentUserWithReauth(email, password)
            _deleteAccountResult.value = result
        }
    }


}
