package com.example.admin.ui.features.auth.restore

import android.os.Bundle
import android.widget.Toast
import com.example.admin.databinding.ActivityRestorePasswordBinding
import com.example.admin.ui.bases.BaseActivity
import com.google.firebase.auth.FirebaseAuth

class RestorePasswordActivity : BaseActivity<ActivityRestorePasswordBinding>() {

    private lateinit var auth: FirebaseAuth

    override fun getViewBinding(): ActivityRestorePasswordBinding {
        return ActivityRestorePasswordBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSendEmail.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Đã gửi email khôi phục mật khẩu", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gửi thất bại: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
