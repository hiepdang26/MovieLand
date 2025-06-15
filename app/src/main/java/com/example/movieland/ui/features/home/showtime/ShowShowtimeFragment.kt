package com.example.movieland.ui.features.home.showtime

import android.app.AlertDialog
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.databinding.FragmentShowShowtimeBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieland.R
import com.example.movieland.data.firebase.model.showtime.FirestoreShowtime
import com.example.movieland.ui.features.home.roomandseat.ChooseSeatFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.ZoneId

@AndroidEntryPoint
class ShowShowtimeFragment : BaseFragment<FragmentShowShowtimeBinding>() {

    private val viewModel: ShowShowtimeViewModel by viewModels()

    private var districtId: String = ""
    private var districtName: String = ""
    private var movieId: String = ""
    private var movieName: String = ""

    private lateinit var showtimeAdapter2D: ShowtimeAdapter
    private lateinit var showtimeAdapter3D: ShowtimeAdapter

    private lateinit var dateAdapter: DateAdapter
    private lateinit var listDate: List<LocalDate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            districtId = it.getString("districtId").orEmpty()
            districtName = it.getString("districtName").orEmpty()
            movieId = it.getString("movieId").orEmpty()
            movieName = it.getString("movieName").orEmpty()
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    private fun setupView() {
        binding.txtNameDistrict.text = districtName
        binding.txtNameMovie.text = movieName

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupInitialData() {
        listDate = generateDates()

        dateAdapter = DateAdapter(listDate) { selectedDate ->
            viewModel.filterShowtimesByDate(selectedDate)
        }

        binding.rcvDate.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = dateAdapter
        }

        showtimeAdapter2D = ShowtimeAdapter { showtime ->
            showConfirmDialog(showtime)
        }

        showtimeAdapter3D = ShowtimeAdapter { showtime ->
            showConfirmDialog(showtime)
        }

        binding.rcvShowtime2d.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = showtimeAdapter2D
        }

        binding.rcvShowtime3d.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = showtimeAdapter3D
        }

        viewModel.loadShowtimes(districtId, movieId)
        viewModel.filterShowtimesByDate(listDate.first())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.showtimes.collectLatest { showtimes ->
                if (showtimes.isNotEmpty()) {
                    viewModel.filterShowtimesByDate(listDate.first())
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.showtimes2D.collectLatest { list2D ->
                showtimeAdapter2D.submitList(list2D)
                if (list2D.isEmpty()) {
                    binding.rcvShowtime2d.visibility = View.GONE
                    binding.txtAlert2d.visibility = View.VISIBLE
                } else {
                    binding.rcvShowtime2d.visibility = View.VISIBLE
                    binding.txtAlert2d.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.showtimes3D.collectLatest { list3D ->
                showtimeAdapter3D.submitList(list3D)
                if (list3D.isEmpty()) {
                    binding.rcvShowtime3d.visibility = View.GONE
                    binding.txtAlert3d.visibility = View.VISIBLE
                } else {
                    binding.rcvShowtime3d.visibility = View.VISIBLE
                    binding.txtAlert3d.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateDates(): List<LocalDate> {
        val today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        return List(7) { index -> today.plusDays(index.toLong()) }
    }

    private fun showConfirmDialog(showtime: FirestoreShowtime) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn chọn xuất chiếu lúc ${showtime.startTime}?")
            .setPositiveButton("Đồng ý") { _, _ ->
                goToChooseSeatFragment(showtime)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
    private fun goToChooseSeatFragment(showtime: FirestoreShowtime) {
        val bundle = Bundle().apply {
            putString("roomId", showtime.roomId)
            putString("showtimeId", showtime.id)
            putString("movieName", showtime.movieName)
            putString("date", showtime.date.toString())
        }

        val fragment = ChooseSeatFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

}
