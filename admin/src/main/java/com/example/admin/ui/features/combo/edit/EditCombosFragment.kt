package com.example.admin.ui.features.combo.edit

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.databinding.FragmentEditCombosBinding
import com.example.admin.ui.bases.BaseFragment

class EditCombosFragment : BaseFragment<FragmentEditCombosBinding>() {

    private val viewModel: EditCombosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditCombosBinding {
        return FragmentEditCombosBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun setupInitialData() {
        TODO("Not yet implemented")
    }

    override fun setupObserver() {
        TODO("Not yet implemented")
    }

    override fun setupClickView() {
        TODO("Not yet implemented")
    }
}