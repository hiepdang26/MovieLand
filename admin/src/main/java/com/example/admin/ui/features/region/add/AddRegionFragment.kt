package com.example.admin.ui.features.region.add

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.databinding.FragmentAddRegionBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddRegionFragment : BaseFragment<FragmentAddRegionBinding>() {

    private val viewModel: AddRegionViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddRegionBinding {
        return FragmentAddRegionBinding.inflate(inflater, container, false)
    }

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
        setupObserver()
    }
    override fun setupInitialData() {
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.addResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), "Thêm nơi thành công", Toast.LENGTH_SHORT).show()
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
        binding.btnSavePlace.setOnClickListener {
            val name = binding.edtPlaceName.text.toString().trim()
            viewModel.addRegion(name)
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
