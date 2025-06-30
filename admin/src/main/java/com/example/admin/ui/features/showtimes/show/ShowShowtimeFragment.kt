package com.example.admin.ui.features.showtimes.show

import android.app.DatePickerDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import com.example.admin.databinding.FragmentShowShowtimeBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.mainmovie.edit.EditMovieFragment
import com.example.admin.ui.features.room.add.AddRoomFragment
import com.example.admin.ui.features.showtimes.add.AddShowtimeFragment
import com.example.admin.ui.features.showtimes.edit.EditShowtimeFragment
import com.example.admin.ui.features.showtimes.show.model.MovieWithShowtimes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ShowShowtimeFragment : BaseFragment<FragmentShowShowtimeBinding>() {

    private var roomId: String = ""
    private var roomName: String = ""
    private var districtId: String = ""
    private var districtName: String = ""
    private var totalSeats: String = ""
    private var seatInEachRow: String = ""


    private var filterDate: Date? = null
    private var currentShowtimes: List<FirestoreShowtime> = emptyList()

    private val viewModel: ShowShowtimeViewModel by viewModels()
    private lateinit var adapter: MovieWithShowtimesAdapter

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentShowShowtimeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).showNavigationBar()
    }

    override fun setupInitialData() {
        roomId = arguments?.getString("roomId") ?: ""
        roomName = arguments?.getString("roomName") ?: ""
        districtId = arguments?.getString("districtId") ?: ""
        districtName = arguments?.getString("districtName") ?: ""
        totalSeats = arguments?.getString("totalSeat") ?: ""
        seatInEachRow = arguments?.getString("seatInEachRow") ?: ""

        if (roomId.isNotEmpty()) {
            viewModel.loadShowtimesByRoom(roomId)
        }
        adapter = MovieWithShowtimesAdapter(emptyList()) { showtimeId ->
            navigateToEditShowtime(showtimeId)
        }
        binding.rcvShowtime.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvShowtime.adapter = adapter

        binding.txtTitle.text = roomName
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.showtimes.collectLatest { showtimes ->
                val grouped = groupShowtimesByMovie(showtimes)
                adapter.submitList(grouped)
                currentShowtimes = showtimes
                updateFilteredList()

            }
        }
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

        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnAdd.setOnClickListener {
            val fragment = AddShowtimeFragment().apply {
                arguments = Bundle().apply {
                    putString("roomId", roomId)
                    putString("roomName", roomName)
                    putString("districtId", districtId)
                    putString("districtName", districtName)
                    putString("totalSeat", totalSeats)
                    putString("seatInEachRow", seatInEachRow)
                }
            }

            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null).commit()
        }
    }

    private fun updateFilteredList() {
        val filtered = filterDate?.let { date ->
            currentShowtimes.filter { showtime ->
                val isSameDay = showtime.startTime?.let { sameDay(it, date) } == true
                Log.d("FilterShowtime", "Showtime: ${showtime.movieName}, startTime: ${showtime.startTime}, isSameDay: $isSameDay")
                isSameDay
            }
        } ?: currentShowtimes

        Log.d("FilterShowtime", "Filter date: ${filterDate}, filtered size: ${filtered.size}")
        val grouped = groupShowtimesByMovie(filtered)
        adapter.submitList(grouped)
    }


    private fun sameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        val result = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        Log.d("SameDay", "date1: $date1, date2: $date2, result: $result")
        return result
    }



    private fun navigateToEditShowtime(showtimeId: String) {
        val fragment = EditShowtimeFragment().apply {
            arguments = Bundle().apply {
                putString("showtimeId", showtimeId)
                putString("roomName", roomName)
                putString("districtId", districtId)
                putString("districtName", districtName)
                putString("totalSeat", totalSeats)
                putString("seatInEachRow", seatInEachRow)
            }
        }

        parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null).commit()
    }
}

