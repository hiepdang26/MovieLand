package com.example.movieland.ui.features.personal.ticket.show

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.MainActivity
import com.example.movieland.R
import com.example.movieland.databinding.FragmentShowBookedTicketsBinding
import com.example.movieland.ui.features.personal.ticket.detail.DetailTicketFragment
import com.google.firebase.auth.FirebaseAuth
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
        setupClickView()
    }

    override fun setupInitialData() {
        val userId = getCurrentUserId()
        setupRecyclerView()
        viewModel.loadBookedTickets(userId)
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterTicketsByMovieName(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
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
                .replace(R.id.fragmentContainerView, detailFragment).addToBackStack(null).commit()
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
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
}
