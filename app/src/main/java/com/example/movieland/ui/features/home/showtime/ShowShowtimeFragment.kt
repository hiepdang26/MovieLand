package com.example.movieland.ui.features.home.showtime

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.databinding.FragmentShowShowtimeBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShowShowtimeFragment : BaseFragment<FragmentShowShowtimeBinding>() {

    companion object {
        fun newInstance() = ShowShowtimeFragment()
    }

    private val viewModel: ShowShowtimeViewModel by viewModels()

    private var districtId: String = ""
    private var movieId: String = ""

    private lateinit var showtimeAdapter: ShowtimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            districtId = it.getString("districtId").orEmpty()
            movieId = it.getString("movieId").orEmpty()
        }
        android.util.Log.d("ShowtimeLog", "movieId: ${movieId}, districtId : ${districtId}")

    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentShowShowtimeBinding {
        return FragmentShowShowtimeBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun setupInitialData() {
        showtimeAdapter = ShowtimeAdapter()

        binding.recyclerShowtime.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = showtimeAdapter
        }

        viewModel.loadShowtimes(districtId, movieId)
    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.showtimes.collectLatest { showtimes ->
                showtimeAdapter.submitList(showtimes)
                // LOG TO CHECK
                showtimes.forEach {
                    android.util.Log.d("ShowtimeLog", "Showtime: id=${it.id}, movie=${it.movieName}, room=${it.roomName}, time=${it.startTime}")
                }
                android.util.Log.d("ShowtimeLog", "Tổng số showtime: ${showtimes.size}")
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }


    override fun setupClickView() {

    }
}
