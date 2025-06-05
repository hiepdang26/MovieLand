package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.showtime

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.R
import com.example.admin.databinding.FragmentChooseShowtimeForTicketBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.ticket.show.ShowTicketFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseShowtimeForTicketFragment : BaseFragment<FragmentChooseShowtimeForTicketBinding>() {

    private val viewModel: ChooseShowtimeForTicketViewModel by viewModels()
    private lateinit var adapter: ChooseShowtimeForTicketAdapter
    private var roomId: String = ""

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentChooseShowtimeForTicketBinding {
        return FragmentChooseShowtimeForTicketBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomId = arguments?.getString("roomId") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun setupInitialData() {
        if (roomId.isNotEmpty()) {
            viewModel.loadShowtimesByRoomId(roomId)
        }
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.showtimes.collectLatest { showtimes ->
                adapter = ChooseShowtimeForTicketAdapter(showtimes) { showtime ->
                    val fragment = ShowTicketFragment().apply {
                        arguments = Bundle().apply {
                            putString("showtimeId", showtime.id)
                            putString("roomId", roomId)
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment).addToBackStack(null).commit()
                }

                binding.rcvShowtime.layoutManager = LinearLayoutManager(requireContext())
                binding.rcvShowtime.adapter = adapter
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
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
    }


}
