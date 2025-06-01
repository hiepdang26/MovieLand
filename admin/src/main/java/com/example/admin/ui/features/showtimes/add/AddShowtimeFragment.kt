package com.example.admin.ui.features.showtimes.add

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.admin.MainActivity
import com.example.admin.data.firebase.model.FirestoreMovie
import com.example.admin.databinding.FragmentAddShowtimeBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddShowtimeFragment : BaseFragment<FragmentAddShowtimeBinding>() {

    private val viewModel: AddShowtimeViewModel by viewModels()

    private val calendar = Calendar.getInstance()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private var selectedDate: Date? = null
    private var selectedStartTime: Date? = null
    private var selectedEndTime: Date? = null
    private var selectedStatus: String = "pending"
    private var selectedScreenType: String = "2D"
    private var selectedScreenCategory: String = "regular"
    private var roomId: String = ""
    private var price: Double = 0.0
    private var roomName: String = ""
    private var districtId: String = ""
    private var districtName: String = ""
    private var totalSeat: String = ""
    private var seatInEachRow: String = ""
    private var movieId: String = ""
    private var movieName: String = ""

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAddShowtimeBinding.inflate(inflater, container, false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()

    }

    override fun setupInitialData() {
        setupStatusSpinner()
        setupScreenTypeSpinner()
        setupScreenCategorySpinner()

        roomId = arguments?.getString("roomId") ?: ""
        roomName = arguments?.getString("roomName") ?: ""
        districtId = arguments?.getString("districtId") ?: ""
        districtName = arguments?.getString("districtName") ?: ""
        totalSeat = arguments?.getString("totalSeat") ?: ""
        seatInEachRow = arguments?.getString("seatInEachRow") ?: ""
        Log.d("AddShowtimeFragment", "setupObserver: ${totalSeat}, ${seatInEachRow}")

        binding.txtRoomName.text = roomName
        binding.txtDistrictName.text = districtName

        binding.txtTotalSeat.text = totalSeat
        binding.txtSeatInEachRow.text = seatInEachRow
    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.movies.collectLatest { movies ->
                setupMovieSpinner(movies)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.saveResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        val newShowtimeId = it.getOrNull() ?: ""
                        Toast.makeText(
                            requireContext(),
                            "Thêm suất chiếu thành công: ID = $newShowtimeId",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetSaveResult()
                }
            }
        }
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnStartTime.setOnClickListener {
            showTimePicker(isStartTime = true)
        }

        binding.btnEndTime.setOnClickListener {
            showTimePicker(isStartTime = false)
        }

        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedStatus = when (position) {
                    0 -> "active"
                    1 -> "pending"
                    2 -> "cancel"
                    else -> "pending"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedStatus = "pending"
            }
        }

        binding.spinnerScreenType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedScreenType = when (position) {
                    0 -> "2D"
                    1 -> "3D"
                    else -> "2D"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedScreenType = "2D"
            }
        }

        binding.spinnerScreenCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedScreenCategory = when (position) {
                    0 -> "regular"
                    1 -> "early"
                    2 -> "vip"
                    else -> "regular"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedScreenCategory = "regular"
            }
        }


        binding.btnSaveShowtime.setOnClickListener {
            saveShowtime()
        }

    }

    private fun setupStatusSpinner() {
        val statusList = listOf("Đang hoạt động", "Đang chờ", "Hủy")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter
        binding.spinnerStatus.setSelection(1)
    }

    private fun setupScreenTypeSpinner() {
        val statusList = listOf("2D", "3D")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerScreenType.adapter = adapter
        binding.spinnerScreenType.setSelection(1)
    }

    private fun setupScreenCategorySpinner() {
        val statusList = listOf("Xuất chiếu sớm", "Thường", "VIP")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerScreenCategory.adapter = adapter
        binding.spinnerScreenCategory.setSelection(1)
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
            binding.btnSelectDate.text = dateFormat.format(selectedDate!!)
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val now = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val time = calendar.time
            if (isStartTime) {
                selectedStartTime = time
                binding.btnStartTime.text = timeFormat.format(time)
            } else {
                selectedEndTime = time
                binding.btnEndTime.text = timeFormat.format(time)
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
    }

    private fun saveShowtime() {

        if (roomId.isBlank() || movieId.isBlank()) {
            Toast.makeText(requireContext(), "Phòng hoặc phim không hợp lệ", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (selectedDate == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày chiếu", Toast.LENGTH_SHORT).show()
            return
        }

        val priceText = binding.edtPrice.text.toString().trim()
         price = priceText.toDoubleOrNull() ?: 0.0

        if (price == 0.0) {
            Toast.makeText(requireContext(), "Giá khung giờ chiếu phải khác 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày chiếu", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStartTime == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn giờ bắt đầu", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedEndTime == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn giờ kết thúc", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (selectedEndTime!!.before(selectedStartTime)) {
            Toast.makeText(
                requireContext(), "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT
            ).show()
            return
        }

        val startTimeFull = mergeDateAndTime(selectedDate!!, selectedStartTime!!)
        val endTimeFull = mergeDateAndTime(selectedDate!!, selectedEndTime!!)
        val totalSeatInt = totalSeat.toIntOrNull() ?: 0
        val seatInEachRowInt = seatInEachRow.toIntOrNull() ?: 0
        viewModel.addShowtime(
            roomId = roomId,
            movieId = movieId,
            movieName = movieName,
            startTime = startTimeFull,
            endTime = endTimeFull,
            date = selectedDate!!,
            status = selectedStatus,
            screenType = selectedScreenType,
            screenCategory = selectedScreenCategory,
            price = price,
            districtId = districtId,
            districtName = districtName,
            roomName = roomName,
            totalSeat = totalSeatInt,
            seatInEachRow = seatInEachRowInt
        )
    }

    private fun setupMovieSpinner(movies: List<FirestoreMovie>) {
        val movieNames = movies.map { it.title }
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, movieNames)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectMovie.adapter = adapter

        binding.spinnerSelectMovie.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val selectedMovie = movies[position]
                    movieId = selectedMovie.id.toString()
                    movieName = selectedMovie.title
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    movieId = ""
                    movieName = ""
                }
            }
    }

    private fun mergeDateAndTime(date: Date, time: Date): Date {
        val calendarDate = Calendar.getInstance()
        calendarDate.time = date

        val calendarTime = Calendar.getInstance()
        calendarTime.time = time

        calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY))
        calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE))
        calendarDate.set(Calendar.SECOND, 0)
        calendarDate.set(Calendar.MILLISECOND, 0)

        return calendarDate.time
    }
}
