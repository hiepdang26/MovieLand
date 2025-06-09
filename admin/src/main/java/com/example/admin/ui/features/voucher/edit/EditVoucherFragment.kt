package com.example.admin.ui.features.voucher.edit

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.R
import com.example.admin.databinding.FragmentAddVoucherBinding
import com.example.admin.databinding.FragmentEditVoucherBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.voucher.add.AddVoucherViewModel

class EditVoucherFragment : BaseFragment<FragmentEditVoucherBinding>() {



    private val viewModel: EditVoucherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditVoucherBinding {
        return FragmentEditVoucherBinding.inflate(inflater, container, false)
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