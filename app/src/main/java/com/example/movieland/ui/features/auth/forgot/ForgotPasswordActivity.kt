package com.example.movieland.ui.features.auth.forgot

import android.os.Bundle
import com.example.movieland.databinding.ActivityForgotPasswordBinding
import android.widget.Toast
import androidx.activity.viewModels
import com.example.admin.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {

    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun getViewBinding(): ActivityForgotPasswordBinding {
        return ActivityForgotPasswordBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnSendEmail.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.sendResetEmail(email)
        }
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.tvHaveAccount.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.resetResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Đã gửi email khôi phục mật khẩu", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this, "Gửi email thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
