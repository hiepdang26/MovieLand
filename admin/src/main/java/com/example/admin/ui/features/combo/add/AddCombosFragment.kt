package com.example.admin.ui.features.combo.add

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.data.firebase.model.combo.FirestoreCombo
import com.example.admin.databinding.FragmentAddCombosBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class AddCombosFragment : BaseFragment<FragmentAddCombosBinding>() {

    private val viewModel: AddCombosViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var districtId: String = ""
    private var districtName: String = ""

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.imgPoster.setImageURI(it)
            }
        }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAddCombosBinding {
        return FragmentAddCombosBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
    }

    override fun setupInitialData() {
        setupStatusSpinner()
        districtId = arguments?.getString("districtId").toString()
        districtName = arguments?.getString("districtName").toString()
        binding.edtDistrictName.text = districtName
    }

    override fun setupObserver() {

        lifecycleScope.launch {
            viewModel.success.collectLatest {
                if (it) {
                    Toast.makeText(requireContext(), "Tạo combo thành công", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest {
                it?.let {
                    Log.d("AddComboFragment", "error when add: $it")
                    viewModel.clearError()
                }
            }
        }
    }

    override fun setupClickView() {
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
            val description = binding.edtDescription.text.toString().trim()
            val price = binding.edtPrice.text.toString().toDoubleOrNull()
            val available = when (binding.spinnerStatus.selectedItem.toString()) {
                "Đang hoạt động" -> true
                "Ngưng hoạt động" -> false
                else -> true
            }
            if (name.isEmpty() || description.isEmpty() || price == null) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn ảnh combo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.uploadComboWithImage(
                imageUri = selectedImageUri!!,
                name = name,
                description = description,
                price = price,
                districtId = districtId,
                districtName = districtName ?: "",
                available = available
            )
        }

    }
    private fun setupStatusSpinner() {
        val statusOptions = listOf("Đang hoạt động", "Ngưng hoạt động")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner_custom, statusOptions)
        adapter.setDropDownViewResource(R.layout.item_spinner_custom)
        binding.spinnerStatus.adapter = adapter
    }

}
