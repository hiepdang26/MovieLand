package com.example.admin.ui.features.ticket.show

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.admin.R
import com.example.admin.databinding.FragmentShowTicketBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.ticket.add.AddTicketFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowTicketFragment : BaseFragment<FragmentShowTicketBinding>() {

    private val viewModel: ShowTicketViewModel by viewModels()
    private lateinit var adapter: ShowTicketAdapter
    private var showtimeId: String = ""
    private var roomId: String = ""


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentShowTicketBinding {
        return FragmentShowTicketBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showtimeId = arguments?.getString("showtimeId") ?: ""
        roomId = arguments?.getString("roomId") ?: ""

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun setupInitialData() {
        if (showtimeId.isNotEmpty()) {
            viewModel.loadTickets(showtimeId)
        } else {
            Toast.makeText(requireContext(), "Không có showtimeId", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.tickets.collectLatest { tickets ->
                adapter = ShowTicketAdapter(tickets)
                binding.rcvTicket.layoutManager = GridLayoutManager(requireContext(), 10)
                binding.rcvTicket.adapter = adapter
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { errorMsg ->
                errorMsg?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnAdd.setOnClickListener {
            val fragment = AddTicketFragment().apply {
                arguments = Bundle().apply {
                    putString("showtimeId", showtimeId)
                    putString("roomId", roomId)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
