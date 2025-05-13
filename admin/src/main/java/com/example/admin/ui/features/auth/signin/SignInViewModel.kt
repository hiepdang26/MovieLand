package com.example.admin.ui.features.auth.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admin.data.firebase.datasource.FirebaseAuthDataSource
import com.example.admin.domain.usecase.firebase.auth.LoginWithEmailUseCase
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
            authDataSource.login(email, password).collect { result ->
                result.onSuccess { user ->
                    val role = authDataSource.getUserRole(user.uid)
                    if (role == "admin") {
                        _loginResult.value = Result.success(user)
                    } else {
                        _loginResult.value = Result.failure(Exception("Tài khoản không có quyền admin"))
                    }
                }.onFailure {
                    _loginResult.value = Result.failure(it)
                }
            }
        }
    }
}

