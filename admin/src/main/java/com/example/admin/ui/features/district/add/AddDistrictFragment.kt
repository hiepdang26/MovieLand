package com.example.admin.ui.features.district.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.admin.databinding.FragmentAddDistrictBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddDistrictFragment : BaseFragment<FragmentAddDistrictBinding>() {

    private val viewModel: AddDistrictViewModel by viewModels()

    private var regionIdSelected: String? = null
    private var regionName: String? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddDistrictBinding {
        return FragmentAddDistrictBinding.inflate(inflater, container, false)
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
        setupClickView()
        setupObserver()
    }

    override fun setupInitialData() {
        regionName = arguments?.getString("regionName")
        regionIdSelected = arguments?.getString("regionIdSelected")
        binding.txtRegionName.text = regionName ?: "Không xác định"
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.addResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Thêm quận/huyện thành công", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.resetAddResult()
                }
            }
        }
    }

    override fun setupClickView() {
        binding.btnSaveDistrict.setOnClickListener {
            val districtName = binding.edtDistrictName.text.toString().trim()
            val regionId = regionIdSelected ?: ""
            val regionName = regionName ?: ""
            viewModel.addDistrict(regionId, regionName, districtName)
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
