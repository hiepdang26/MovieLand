package com.example.movieland.ui.features.personal.information

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.R
import com.example.movieland.databinding.FragmentPersonalInformationBinding
import com.example.movieland.ui.features.auth.signin.SignInActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class PersonalInformationFragment : BaseFragment<FragmentPersonalInformationBinding>() {
    private val viewModel: PersonalInformationViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPersonalInformationBinding {
        return FragmentPersonalInformationBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun setupInitialData() {
        viewModel.loadCurrentUser()
        setupGenderSpinner()
    }

    override fun setupObserver() {
        viewModel.userData.observe(viewLifecycleOwner) { data ->
            data?.let {
                binding.edtFullName.setText(it["name"] as? String ?: "")
                binding.edtBirthdate.setText(it["birthdate"] as? String ?: "")
                binding.edtEmail.setText(it["email"] as? String ?: "")
                binding.edtNumber.setText(it["phone"] as? String ?: "")

                val gender = it["gender"] as? String ?: ""
                val genderPosition = genderOptions.indexOfFirst { option ->
                    option.equals(gender, ignoreCase = true)
                }
                if (genderPosition >= 0) {
                    binding.spinnerGender.setSelection(genderPosition)
                }

                val avatarResId = when (gender.lowercase()) {
                    "nam" -> R.drawable.male_ava
                    "nữ", "nu" -> R.drawable.female_ava
                    else -> R.drawable.male_ava
                }
                Glide.with(requireContext()).load(avatarResId).into(binding.imgProfile)
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            binding.btnSave.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            result.onSuccess {
                Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            }
            result.onFailure {
                Toast.makeText(
                    requireContext(), "Cập nhật thất bại: ${it.message}", Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.deleteAccountResult.observe(viewLifecycleOwner) { result ->
            binding.btnDeleteAccount.visibility = View.VISIBLE
            binding.progressBarDelete.visibility = View.GONE
            result.onSuccess {
                Toast.makeText(requireContext(), "Đã xóa tài khoản", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            result.onFailure {
                Log.e("PersonalInformationFragment", " ${it.message}")
                Toast.makeText(requireContext(), "Không thể xóa tài khoản", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun showDeleteAccountDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reauth, null)
        val edtEmail = dialogView.findViewById<EditText>(R.id.edt_email)
        val edtPassword = dialogView.findViewById<EditText>(R.id.edt_password)

        AlertDialog.Builder(requireContext()).setTitle("Xác thực lại để xóa tài khoản")
            .setView(dialogView).setPositiveButton("Xóa") { _, _ ->
                binding.btnDeleteAccount.visibility = View.GONE
                binding.progressBarDelete.visibility = View.VISIBLE
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                viewModel.deleteAccountWithReauth(email, password)
            }.setNegativeButton("Hủy", null).show()
    }

    override fun setupClickView() {
        binding.btnDeleteAccount.setOnClickListener {

            showDeleteAccountDialog()
        }

        binding.edtBirthdate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog =
                DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    val dateStr =
                        "%02d/%02d/%04d".format(selectedDay, selectedMonth + 1, selectedYear)
                    binding.edtBirthdate.setText(dateStr)
                }, year, month, day)
            dialog.show()
        }

        binding.btnSave.setOnClickListener {

            val name = binding.edtFullName.text.toString().trim()
            val gender = binding.spinnerGender.selectedItem?.toString() ?: ""
            val birthdate = binding.edtBirthdate.text.toString().trim()
            val phone = binding.edtNumber.text.toString().trim()

            if (name.isBlank() || gender.isBlank() || birthdate.isBlank() || phone.isBlank()) {
                Toast.makeText(
                    requireContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.updateUser(name, gender, birthdate, phone)
            binding.btnSave.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private val genderOptions = listOf("Nam", "Nữ")
    private fun setupGenderSpinner() {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
    }


}