package com.example.admin.ui.features.auth.signin

import android.os.Bundle
import com.example.admin.databinding.ActivitySignInBinding
import com.example.admin.ui.bases.BaseActivity
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import com.example.admin.MainActivity
import com.example.admin.ui.features.auth.restore.RestorePasswordActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : BaseActivity<ActivitySignInBinding>() {

    private val viewModel: SignInViewModel by viewModels()

    override fun getViewBinding(): ActivitySignInBinding {
        return ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(binding.root)

        onClickView()
        observeViewModel()
    }

    private fun onClickView() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, RestorePasswordActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure {
                Toast.makeText(this, "Đăng nhập thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
