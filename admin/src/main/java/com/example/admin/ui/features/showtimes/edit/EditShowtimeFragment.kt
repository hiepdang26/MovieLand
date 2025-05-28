package com.example.admin.ui.features.showtimes.edit

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.databinding.FragmentEditShowtimeBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

class EditShowtimeFragment :  BaseFragment<FragmentEditShowtimeBinding>() {

    private var districtId: String = ""
    private var districtName: String = ""

    private val viewModel: EditShowtimeViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEditShowtimeBinding {
        return FragmentEditShowtimeBinding.inflate(layoutInflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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