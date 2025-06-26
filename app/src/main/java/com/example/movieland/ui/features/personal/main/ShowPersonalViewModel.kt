package com.example.movieland.ui.features.personal.main

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
            val data = authDataSource.getCurrentUserData()
            _userData.value = data
        }
    }
}