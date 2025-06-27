package com.example.movieland.ui.features.personal.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.R
import com.example.movieland.databinding.FragmentShowPersonalBinding
import com.example.movieland.ui.features.auth.signin.SignInActivity
import com.example.movieland.ui.features.auth.signup.SignUpActivity
import com.example.movieland.ui.features.personal.information.PersonalInformationFragment
import com.example.movieland.ui.features.personal.ticket.ShowBookedTicketsFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowPersonalFragment : BaseFragment<FragmentShowPersonalBinding>() {
    private val viewModel: ShowPersonalViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentShowPersonalBinding {
        return FragmentShowPersonalBinding.inflate(inflater, container, false)
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
    }

    override fun setupObserver() {
        viewModel.userData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.tvUserName.text = data["name"] as? String ?: ""
                binding.tvUserEmail.text = data["email"] as? String ?: ""

                val gender = data["gender"] as? String ?: ""

                val avatarResId = when (gender.lowercase()) {
                    "nam" -> R.drawable.male_ava
                    "nữ", "nu" -> R.drawable.female_ava
                    else -> R.drawable.male_ava
                }

                Glide.with(requireContext()).load(avatarResId).into(binding.imgProfile)
            }
        }
        viewModel.changePasswordResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
            }
            result.onFailure { e ->
                val message = e.message ?: ""
                if (
                    message.contains("The supplied auth credential is incorrect", ignoreCase = true) ||
                    message.contains("The password is invalid", ignoreCase = true)
                ) {
                    Toast.makeText(requireContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Đổi mật khẩu thất bại: $message", Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    override fun setupClickView() {
        binding.btnChangePassword.setOnClickListener {
            showChangePasswordBottomSheet()
        }
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
        binding.btnEditProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, PersonalInformationFragment())
                .addToBackStack(null).commit()
        }
        binding.btnHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ShowBookedTicketsFragment())
                .addToBackStack(null).commit()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                performLogout()
            }.setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun performLogout() {
        try {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            val intent = Intent(requireContext(), SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Lỗi khi đăng xuất: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun showChangePasswordBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_bottom_change_password, null)
        dialog.setContentView(view)

        val edtOldPassword = view.findViewById<EditText>(R.id.edtOldPassword)
        val edtNewPassword = view.findViewById<EditText>(R.id.edtNewPassword)
        val edtConfirmPassword = view.findViewById<EditText>(R.id.edtConfirmPassword)
        val btnConfirm = view.findViewById<View>(R.id.btnConfirmChange)

        btnConfirm.setOnClickListener {
            val oldPassword = edtOldPassword.text.toString()
            val newPassword = edtNewPassword.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()
            val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

            if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "Mật khẩu mới không trùng khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword.length < 6) {
                Toast.makeText(requireContext(), "Mật khẩu mới phải từ 6 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.changePassword(email, oldPassword, newPassword)
            dialog.dismiss()

        }



        dialog.show()
    }

    interface LogoutCallback {
        fun onLogoutSuccess()
    }
}