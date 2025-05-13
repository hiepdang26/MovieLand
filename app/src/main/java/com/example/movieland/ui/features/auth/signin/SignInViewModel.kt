package com.example.movieland.ui.features.auth.signin

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
class SignInViewModel @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<FirebaseUser>>()
    val loginResult: LiveData<Result<FirebaseUser>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authDataSource.login(email, password)
            result.onSuccess { user ->
                val role = authDataSource.getUserRole(user.uid)
                if (role == null || role == "user") {
                    _loginResult.value = Result.success(user)
                } else {
                    _loginResult.value = Result.failure(Exception("Tài khoản không dành cho app khách hàng"))
                }
            }.onFailure {
                _loginResult.value = Result.failure(it)
            }
        }
    }
}
