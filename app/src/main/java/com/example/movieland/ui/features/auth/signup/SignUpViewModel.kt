package com.example.movieland.ui.features.auth.signup


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieland.data.firebase.datasource.FirebaseAuthDataSource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
) : ViewModel() {

    private val _signUpResult = MutableLiveData<Result<FirebaseUser>>()
    val signUpResult: LiveData<Result<FirebaseUser>> = _signUpResult

    fun signUp(
        email: String,
        password: String,
        name: String,
        phone: String,
        birthdate: String
    ) {
        viewModelScope.launch {
            val result = authDataSource.registerUser(email, password, name, phone, birthdate)
            _signUpResult.value = result
        }
    }
}
