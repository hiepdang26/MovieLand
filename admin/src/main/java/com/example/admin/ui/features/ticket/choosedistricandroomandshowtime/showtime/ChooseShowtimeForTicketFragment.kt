package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.showtime

import android.app.DatePickerDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.R
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import com.example.admin.databinding.FragmentChooseShowtimeForTicketBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.showtimes.show.model.MovieWithShowtimes
import com.example.admin.ui.features.ticket.show.ShowTicketFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ChooseShowtimeForTicketFragment : BaseFragment<FragmentChooseShowtimeForTicketBinding>() {

    private val viewModel: ChooseShowtimeForTicketViewModel by viewModels()
    private lateinit var adapter: MovieWithShowtimesAdapter
    private var roomId: String = ""


    private var filterDate: Date? = null
    private var currentShowtimes: List<FirestoreShowtime> = emptyList()


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
                val grouped = viewModel.groupShowtimesByMovie(showtimes)
                adapter = MovieWithShowtimesAdapter(grouped) { showtimeId ->
                    val fragment = ShowTicketFragment().apply {
                        arguments = Bundle().apply {
                            putString("showtimeId", showtimeId)
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
    private fun updateFilteredList() {
        val filtered = filterDate?.let { date ->
            currentShowtimes.filter { showtime ->
                val isSameDay = showtime.startTime?.let { sameDay(it, date) } == true
                isSameDay
            }
        } ?: currentShowtimes

        val grouped = groupShowtimesByMovie(filtered)
        adapter.submitList(grouped)
    }


    private fun sameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        val result = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        return result
    }
    private fun groupShowtimesByMovie(showtimes: List<FirestoreShowtime>): List<MovieWithShowtimes> {
        return showtimes.groupBy { it.movieName }.map { (movieName, showtimeList) ->
            MovieWithShowtimes(movieName, showtimeList)
        }
    }

    override fun setupClickView() {

        binding.btnFilter.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(
                requireContext(), { _, year, month, dayOfMonth ->
                    val cal = Calendar.getInstance()
                    cal.set(year, month, dayOfMonth, 0, 0, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    filterDate = cal.time
                    updateFilteredList()
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


}
