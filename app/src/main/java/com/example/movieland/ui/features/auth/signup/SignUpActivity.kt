package com.example.movieland.ui.features.auth.signup

import android.os.Bundle
import android.app.DatePickerDialog
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.admin.ui.bases.BaseActivity
import com.example.movieland.R
import com.example.movieland.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {

    private val viewModel: SignUpViewModel by viewModels()

    override fun getViewBinding(): ActivitySignUpBinding {
        return ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnCalendar.setOnClickListener {
            showDatePicker()
        }
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.tvHaveAccount.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val name = binding.edtFullName.text.toString().trim()
            val phone = binding.edtPhone.text.toString().trim()
            val birthdate = binding.edtDateOfBirth.text.toString().trim()
            val selectedGenderId = binding.rgGender.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                R.id.rbMale -> "Nam"
                R.id.rbFemale -> "Nữ"
                else -> ""
            }
            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty() || birthdate.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading(true)
            viewModel.signUp(email, password, name, phone, birthdate, gender)

        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val birthdate = "%02d/%02d/%04d".format(selectedDay, selectedMonth + 1, selectedYear)
            binding.edtDateOfBirth.setText(birthdate)
        }, year, month, day)

        dialog.show()
    }

    private fun observeViewModel() {
        viewModel.signUpResult.observe(this) { result ->
            showLoading(false)
            result.onSuccess {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this, "Đăng ký thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        binding.btnRegister.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.lottieLoading.visibility = if (isLoading) View.VISIBLE else View.GONE

        if (isLoading) binding.lottieLoading.playAnimation()
        else binding.lottieLoading.cancelAnimation()
    }

}
