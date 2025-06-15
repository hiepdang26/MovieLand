package com.example.admin.ui.features.combo.edit

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.admin.MainActivity
import com.example.admin.data.firebase.model.combo.FirestoreCombo
import com.example.admin.databinding.FragmentEditCombosBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditCombosFragment : BaseFragment<FragmentEditCombosBinding>() {

    private val viewModel: EditCombosViewModel by viewModels()
    private var comboId: String? = null
    private var selectedImageUri: Uri? = null
    private var currentImageUrl: String = ""

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.imgPoster.setImageURI(it)
            }
        }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditCombosBinding {
        return FragmentEditCombosBinding.inflate(inflater, container, false)
    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comboId = arguments?.getString("comboId")
        setupStatusSpinner()
        setupClickView()
        setupObserver()
        comboId?.let { viewModel.loadCombo(it) }
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.combo.collectLatest { combo ->
                combo?.let {
                    binding.edtDistrictName.text = it.districtName
                    binding.edtName.setText(it.name)
                    binding.edtDescription.setText(it.description)
                    binding.edtPrice.setText(it.price.toString())
                    currentImageUrl = it.imageUrl

                    if (selectedImageUri == null && currentImageUrl.isNotBlank()) {
                        Glide.with(requireContext()).load(currentImageUrl).into(binding.imgPoster)
                    }

                    binding.spinnerStatus.setSelection(if (it.isAvailable) 0 else 1)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isUpdating.collectLatest {
                binding.btnSave.isEnabled = !it
            }
        }

        lifecycleScope.launch {
            viewModel.updateSuccess.collectLatest {
                Toast.makeText(requireContext(), "✔️ Cập nhật thành công", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.deleteSuccess.collectLatest {
                Toast.makeText(requireContext(), "🗑️ Xoá thành công", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }


    }

    override fun setupClickView() {
        binding.btnDelete.setOnClickListener {
            comboId?.let { id ->
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc chắn muốn xoá combo này?")
                    .setPositiveButton("Xoá") { _, _ ->
                        viewModel.deleteCombo(id)
                    }
                    .setNegativeButton("Huỷ", null)
                    .show()
            }
        }


        binding.btnChoosePoster.setOnClickListener {
            if (!PermissionHelper.hasStoragePermission(requireActivity())) {
                PermissionHelper.requestStoragePermission(requireActivity())
            } else {
                pickImageLauncher.launch("image/*")
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val desc = binding.edtDescription.text.toString().trim()
            val price = binding.edtPrice.text.toString().toDoubleOrNull()
            val status = binding.spinnerStatus.selectedItemPosition == 0

            if (name.isEmpty() || price == null) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ tên và giá", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            comboId?.let { id ->
                lifecycleScope.launch {
                    var imageUrl = currentImageUrl

                    selectedImageUri?.let { uri ->
                        val uploadResult = viewModel.uploadImageToCloudinary(uri)
                        if (uploadResult.isFailure) {
                            Toast.makeText(requireContext(), "❌ Upload ảnh thất bại", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        imageUrl = uploadResult.getOrThrow()
                    }

                    val updatedMap = mapOf(
                        "name" to name,
                        "description" to desc,
                        "price" to price,
                        "isAvailable" to status,
                        "imageUrl" to imageUrl,
                        "updatedAt" to com.google.firebase.Timestamp.now()
                    )

                    viewModel.updateCombo(id, updatedMap)
                }
            }
        }
    }

    private fun setupStatusSpinner() {
        val statusOptions = listOf("Đang hoạt động", "Ngưng hoạt động")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter
    }

    override fun setupInitialData() {}
}
