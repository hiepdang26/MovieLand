package com.example.movieland.ui.features.voucher.show

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.MainActivity
import com.example.movieland.R
import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.example.movieland.databinding.FragmentShowVoucherBinding
import com.example.movieland.ui.features.voucher.detail.DetailVoucherFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowVoucherFragment : BaseFragment<FragmentShowVoucherBinding>() {
    private val viewModel: ShowVoucherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentShowVoucherBinding {
        return FragmentShowVoucherBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun setupInitialData() {
        viewModel.loadVouchers()
    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.vouchers.collect { vouchers ->
                val adapter = VoucherAdapter(vouchers) { selectedVoucher ->
                    openEditVoucherFragment(selectedVoucher)
                }
                binding.rcvVoucher.adapter = adapter
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcvVoucher.layoutManager = LinearLayoutManager(requireContext())
        setupObserver()
        setupClickView()
        setupInitialData()
    }
    override fun setupClickView() {

    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).showNavigationBar()

    }

    private fun openEditVoucherFragment(voucher: FirestoreVoucher) {
        val editFragment = DetailVoucherFragment().apply {
            arguments = Bundle().apply {
                putString("voucherId", voucher.id)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, editFragment)
            .addToBackStack(null)
            .commit()
    }
}