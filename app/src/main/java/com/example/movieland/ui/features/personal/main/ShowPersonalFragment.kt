package com.example.movieland.ui.features.personal.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.R
import com.example.movieland.databinding.FragmentShowPersonalBinding
import com.example.movieland.ui.features.auth.signin.SignInActivity
import com.example.movieland.ui.features.auth.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class ShowPersonalFragment : BaseFragment<FragmentShowPersonalBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentShowPersonalBinding {
        return FragmentShowPersonalBinding.inflate(inflater, container, false)
    }
    private val viewModel : ShowPersonalViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    "nam" -> R.drawable.male_ava      // ảnh nam, ví dụ avatar_male.png trong res/drawable
                    "nữ", "nu" -> R.drawable.female_ava  // ảnh nữ, ví dụ avatar_female.png trong res/drawable
                    else -> R.drawable.male_ava    // ảnh mặc định nếu chưa chọn
                }

                Glide.with(requireContext())
                    .load(avatarResId)
                    .into(binding.imgProfile)
            }
        }
    }


    override fun setupClickView() {
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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
            Toast.makeText(requireContext(), "Lỗi khi đăng xuất: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogoutWithCallback() {
        try {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show()

            if (requireActivity() is LogoutCallback) {
                (requireActivity() as LogoutCallback).onLogoutSuccess()
            }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Lỗi khi đăng xuất: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    interface LogoutCallback {
        fun onLogoutSuccess()
    }
}