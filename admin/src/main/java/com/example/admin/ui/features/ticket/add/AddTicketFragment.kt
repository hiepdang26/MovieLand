package com.example.admin.ui.features.ticket.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.admin.databinding.FragmentAddTicketBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddTicketFragment : BaseFragment<FragmentAddTicketBinding>() {

    private val viewModel: AddTicketViewModel by viewModels()
    private var roomId = ""
    private var showtimeId = ""

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddTicketBinding {
        return FragmentAddTicketBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showtimeId = arguments?.getString("showtimeId") ?: ""
        roomId = arguments?.getString("roomId") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickView()
        setupObserver()
    }

    override fun setupInitialData() {
    }

    override fun setupClickView() {
        binding.btnGenerateTicket.setOnClickListener {
            if (showtimeId.isNotEmpty() && roomId.isNotEmpty()) {
                viewModel.generateTickets(showtimeId, roomId)
            } else {
                Toast.makeText(requireContext(), "Thiếu roomId hoặc showtimeId", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.success.collectLatest { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Tạo vé thành công!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { err ->
                err?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
    }
}
