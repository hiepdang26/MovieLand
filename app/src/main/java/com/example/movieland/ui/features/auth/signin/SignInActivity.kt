package com.example.movieland.ui.features.auth.signin

import android.os.Bundle
import com.example.movieland.databinding.ActivitySignInBinding
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.admin.ui.bases.BaseActivity
import com.example.movieland.MainActivity
import com.example.movieland.ui.features.auth.forgot.ForgotPasswordActivity
import com.example.movieland.ui.features.auth.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : BaseActivity<ActivitySignInBinding>() {

    private val viewModel: SignInViewModel by viewModels()

    override fun getViewBinding(): ActivitySignInBinding {
        return ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        _binding = getViewBinding()
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            binding.btnSignIn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        binding.tvHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            binding.btnSignIn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            result.onSuccess {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { e ->
                val message = e.message ?: ""
                if (message.contains("The supplied auth credential is incorrect", ignoreCase = true) ||
                    message.contains("The password is invalid", ignoreCase = true) ||
                    message.contains("There is no user record", ignoreCase = true)
                ) {
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Đăng nhập thất bại: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
