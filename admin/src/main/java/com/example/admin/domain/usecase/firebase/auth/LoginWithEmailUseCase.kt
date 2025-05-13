package com.example.admin.domain.usecase.firebase.auth

import com.example.admin.data.firebase.datasource.FirebaseAuthDataSource
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
) {
    operator fun invoke(email: String, password: String) = authDataSource.login(email, password)
}
