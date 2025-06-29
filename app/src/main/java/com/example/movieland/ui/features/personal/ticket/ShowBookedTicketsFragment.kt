package com.example.movieland.ui.features.personal.ticket

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.R
import com.example.movieland.databinding.FragmentShowBookedTicketsBinding
import com.example.movieland.ui.features.personal.ticket.detail.DetailTicketFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowBookedTicketsFragment : BaseFragment<FragmentShowBookedTicketsBinding>() {
    private val viewModel: ShowBookedTicketsViewModel by viewModels()
    private lateinit var adapter: BookedTicketAdapter

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentShowBookedTicketsBinding {
        return FragmentShowBookedTicketsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
    }

    override fun setupInitialData() {
        val userId = getCurrentUserId()
        setupRecyclerView()
        viewModel.loadBookedTickets(userId)
        binding.edtSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterTicketsByMovieName(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })


    }

    private fun setupRecyclerView() {
        adapter = BookedTicketAdapter { bookingGroup ->
            val bundle = Bundle().apply {
                putParcelableArrayList("tickets", ArrayList(bookingGroup.tickets))
                putString("bookingId", bookingGroup.bookingId)
            }
            val detailFragment = DetailTicketFragment()
            detailFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rcvTicket.adapter = adapter
    }

    override fun setupObserver() {
        viewModel.filteredTickets.observe(viewLifecycleOwner) { tickets ->
            val groups = tickets.filter { !it.bookingId.isNullOrEmpty() }.groupBy { it.bookingId }
                .map { (bookingId, tickets) -> BookingGroup(bookingId ?: "", tickets) }
            adapter.submitList(groups)
        }

    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun getCurrentUserId(): String {
        return com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
}
