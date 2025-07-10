package com.example.admin.ui.features.district.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.admin.databinding.FragmentEditDistrictBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditDistrictFragment : BaseFragment<FragmentEditDistrictBinding>() {

    private val viewModel: EditDistrictViewModel by viewModels()

    private var regionId: String= ""
    private var districtId: String= ""
    private var regionName: String= ""

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditDistrictBinding {
        return FragmentEditDistrictBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
        regionId = arguments?.getString("regionId").toString()
        districtId = arguments?.getString("districtId").toString()
        regionName = arguments?.getString("regionName").toString()
        Log.d("EditDistrictFragment", districtId)
        Log.d("EditDistrictFragment", regionId)

        viewModel.loadDistrict(regionId, districtId)

    }

    override fun setupObserver() {
        binding.txtRegionName.text = regionName ?: "Không xác định"

        lifecycleScope.launch {
            viewModel.district.collectLatest { district ->
                district?.let {
                    Log.d("EditDistrictFragment", it.name)
                    binding.edtDistrictName.setText(it.name)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.updateResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Lỗi cập nhật: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.resetResults()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.deleteResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Lỗi xóa: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.resetResults()
                }
            }
        }
    }

    override fun setupClickView() {
        binding.btnSaveDistrict.setOnClickListener {
            val newName = binding.edtDistrictName.text.toString().trim()

            if (regionId != null && districtId != null) {
                viewModel.updateDistrict(regionId!!, districtId!!, newName, regionName ?: "")
            }
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa quận/huyện này không?")
                .setPositiveButton("Xóa") { dialog, _ ->
                    if (regionId != null && districtId != null) {
                        viewModel.deleteDistrict(regionId!!, districtId!!)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
