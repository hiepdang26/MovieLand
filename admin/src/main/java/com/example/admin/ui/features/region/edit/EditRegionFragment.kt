package com.example.admin.ui.features.region.edit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.admin.databinding.FragmentEditRegionBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditRegionFragment : BaseFragment<FragmentEditRegionBinding>() {

    private val viewModel: EditRegionViewModel by viewModels()
    private var regionId: String = ""

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentEditRegionBinding {
        return FragmentEditRegionBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         regionId = arguments?.getString("regionId") ?: ""
        if (regionId.isNotBlank()) {
            viewModel.loadRegion(regionId)
        } else {
            Toast.makeText(requireContext(), "Region ID không hợp lệ", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
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
        setupClickView()
        setupObserver()
    }

    override fun setupInitialData() {
        lifecycleScope.launch {
            viewModel.region.collectLatest { region ->
                region?.let {
                    binding.edtPlaceName.setText(it.name)
                }
            }
        }
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.updateResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.loadRegion(regionId)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetUpdateResult()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updateResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Thao tác thành công", Toast.LENGTH_SHORT)
                            .show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetUpdateResult()
                }
            }
        }
    }

    override fun setupClickView() {
        binding.btnSavePlace.setOnClickListener {
            val name = binding.edtPlaceName.text.toString().trim()
            viewModel.updateRegion(name)
        }
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa vùng này không?")
                .setPositiveButton("Xóa") { dialog, _ ->
                    viewModel.deleteRegion()
                    dialog.dismiss()
                }.setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
