package com.example.admin.ui.features.showtimes.show

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

@AndroidEntryPoint
class ShowShowtimeFragment : BaseFragment<FragmentShowShowtimeBinding>() {

    private var roomId: String = ""
    private var roomName: String = ""
    private var districtId: String = ""
    private var districtName: String = ""
    private var totalSeats: String = ""
    private var seatInEachRow: String = ""

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
            navigateToEditShowtime(showtimeId, )
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
            }
        }
    }

    private fun groupShowtimesByMovie(showtimes: List<FirestoreShowtime>): List<MovieWithShowtimes> {
        return showtimes.groupBy { it.movieName }
            .map { (movieName, showtimeList) ->
                MovieWithShowtimes(movieName, showtimeList)
            }
    }

    override fun setupClickView() {
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

    private fun navigateToEditShowtime(showtimeId: String ) {
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

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}

