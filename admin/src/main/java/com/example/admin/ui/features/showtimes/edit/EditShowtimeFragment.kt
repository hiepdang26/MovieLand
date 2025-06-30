package com.example.admin.ui.features.showtimes.edit

import android.app.AlertDialog
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
import com.example.admin.R
import com.example.admin.data.firebase.model.FirestoreMovie
import com.example.admin.databinding.FragmentEditShowtimeBinding
import com.example.admin.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditShowtimeFragment : BaseFragment<FragmentEditShowtimeBinding>() {

    private val viewModel: EditShowtimeViewModel by viewModels()

    private var showtimeId: String = ""

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFullFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var selectedDate: Date? = null
    private var selectedStartTime: Date? = null
    private var selectedEndTime: Date? = null
    private var selectedStatus: String = "pending"
    private var selectedScreenType: String = "2D"
    private var selectedScreenCategory: String = "regular"
    private var roomId: String = ""
    private var price: Double = 0.0
    private var roomName: String = ""
    private var districtName: String = ""
    private var totalSeat: Int = 0
    private var seatInEachRow: Int = 0

    private var movieId: String = ""
    private var movieName: String = ""
    private var moviesList: List<FirestoreMovie> = emptyList()

    private var isSpinnerInitialized = false

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEditShowtimeBinding.inflate(inflater, container, false)

    override fun setupInitialData() {
        showtimeId = arguments?.getString("showtimeId") ?: ""
        if (showtimeId.isNotEmpty()) {
            viewModel.loadShowtime(showtimeId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupObserver()
        setupClickView()
    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.showtimeDetail.collectLatest { showtime ->
                showtime?.let {
                    Log.d(
                        "EditShowtimeFragment",
                        "Loaded showtime data: movieId='${it.movieId}', movieName='${it.movieName}'"
                    )

                    selectedDate = it.date
                    selectedStartTime = it.startTime
                    selectedEndTime = it.endTime
                    selectedStatus = it.status
                    selectedScreenType = it.screenType
                    selectedScreenCategory = it.screenCategory
                    price = it.price
                    roomId = it.roomId
                    roomName = it.roomName ?: ""
                    districtName = it.districtName ?: ""
                    totalSeat = it.totalSeat ?: 0
                    seatInEachRow = it.seatInEachRow ?: 0
                    movieId = it.movieId
                    movieName = it.movieName

                    binding.btnSelectDate.text =
                        selectedDate?.let(dateFullFormat::format) ?: "Chọn ngày"
                    binding.btnStartTime.text =
                        selectedStartTime?.let(dateFormat::format) ?: "Chọn giờ bắt đầu"
                    binding.btnEndTime.text =
                        selectedEndTime?.let(dateFormat::format) ?: "Chọn giờ kết thúc"

                    binding.txtRoomName.text = roomName
                    binding.txtDistrictName.text = districtName
                    binding.txtTotalSeat.text = totalSeat.toString()
                    binding.txtSeatInEachRow.text = seatInEachRow.toString()
                    binding.edtPrice.setText(price.toString())

                    isSpinnerInitialized = false

                    val statusIndex = when (selectedStatus) {
                        "active" -> 0
                        "pending" -> 1
                        "cancel" -> 2
                        else -> 1
                    }
                    binding.spinnerStatus.setSelection(statusIndex)

                    val screenTypeIndex = when (selectedScreenType) {
                        "2D" -> 0
                        "3D" -> 1
                        else -> 0
                    }
                    binding.spinnerScreenType.setSelection(screenTypeIndex)

                    val screenCategoryIndex = when (selectedScreenCategory) {
                        "regular", "Xuất chiếu thường" -> 0
                        "early", "Xuất chiếu sớm" -> 1
                        "vip", "Vip" -> 2
                        else -> 0
                    }
                    binding.spinnerScreenCategory.setSelection(screenCategoryIndex)

                    isSpinnerInitialized = true

                    updateMovieSpinnerSelection()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.movies.collectLatest { movies ->
                moviesList = movies
                updateMovieSpinnerSelection()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(
                            requireContext(), "Cập nhật suất chiếu thành công", Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetUpdateResult()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(
                            requireContext(), "Xóa suất chiếu thành công", Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetDeleteResult()
                }
            }
        }
    }

    private fun updateMovieSpinnerSelection() {
        val movieNames = moviesList.map { it.title }
        val adapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner_custom, movieNames)
        adapter.setDropDownViewResource(R.layout.item_spinner_custom)
        binding.spinnerSelectMovie.adapter = adapter

        val currentIndex = moviesList.indexOfFirst { it.id.toString().trim() == movieId.trim() }
        if (currentIndex >= 0) {
            binding.spinnerSelectMovie.setSelection(currentIndex)
        }

        binding.spinnerSelectMovie.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val selectedMovie = moviesList[position]
                    movieId = selectedMovie.id.toString()
                    movieName = selectedMovie.title
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    movieId = ""
                    movieName = ""
                }
            }
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        binding.btnSelectDate.setOnClickListener { showDatePicker() }
        binding.btnStartTime.setOnClickListener { showTimePicker(isStartTime = true) }
        binding.btnEndTime.setOnClickListener { showTimePicker(isStartTime = false) }

        val statusList = listOf("Đang hoạt động", "Đang chờ", "Đã hủy")
        val statusAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner_custom, statusList)
        statusAdapter.setDropDownViewResource(R.layout.item_spinner_custom)
        binding.spinnerStatus.adapter = statusAdapter

        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (isSpinnerInitialized) {
                    selectedStatus = when (position) {
                        0 -> "active"
                        1 -> "pending"
                        2 -> "cancel"
                        else -> "pending"
                    }
                }
                Log.d("SpinnerStatus", "User chọn: $selectedStatus")

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (isSpinnerInitialized) selectedStatus = "pending"
            }
        }

        val screenTypeList = listOf("2D", "3D")
        val screenTypeAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner_custom, screenTypeList)
        screenTypeAdapter.setDropDownViewResource(R.layout.item_spinner_custom)
        binding.spinnerScreenType.adapter = screenTypeAdapter

        binding.spinnerScreenType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (isSpinnerInitialized) {
                        selectedScreenType = when (position) {
                            0 -> "2D"
                            1 -> "3D"
                            else -> "2D"
                        }
                        Log.d("SpinnerStatus", "User chọn: $selectedScreenType")

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    if (isSpinnerInitialized) selectedScreenType = "2D"
                }
            }

        val screenCategoryList = listOf("Xuất chiếu thường", "Xuất chiếu sớm", "Vip")
        val screenCategoryAdapter =
            ArrayAdapter(requireContext(), R.layout.item_spinner_custom, screenCategoryList)
        screenCategoryAdapter.setDropDownViewResource(R.layout.item_spinner_custom)
        binding.spinnerScreenCategory.adapter = screenCategoryAdapter

        binding.spinnerScreenCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (isSpinnerInitialized) {
                        selectedScreenCategory = when (position) {
                            0 -> "regular"
                            1 -> "early"
                            2 -> "vip"
                            else -> "regular"
                        }
                        Log.d("SpinnerStatus", "User chọn: $selectedScreenCategory")

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    if (isSpinnerInitialized) selectedScreenCategory = "regular"
                }
            }

        binding.btnSaveShowtime.setOnClickListener {
            Log.d(
                "SaveShowtime",
                "selectedStatus=$selectedStatus, selectedScreenType=$selectedScreenType, selectedScreenCategory=$selectedScreenCategory"
            )
            saveShowtime()
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa suất chiếu này không?")
                .setPositiveButton("Xóa") { dialog, _ ->
                    viewModel.deleteShowtime(showtimeId)
                    dialog.dismiss()
                }.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }.show()
        }
    }

    private fun saveShowtime() {
        val priceText = binding.edtPrice.text.toString().trim()
        price = priceText.toDoubleOrNull() ?: 0.0

        if (price == 0.0) {
            Toast.makeText(requireContext(), "Giá khung giờ chiếu phải khác 0", Toast.LENGTH_SHORT).show()
            return
        }
        if (movieId.isBlank()) {
            Toast.makeText(requireContext(), "Vui lòng chọn phim", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Vui lòng chọn giờ kết thúc", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedEndTime!!.before(selectedStartTime)) {
            Toast.makeText(
                requireContext(), "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT
            ).show()
            return
        }

        // KIỂM TRA NGÀY PHẢI >= HÔM NAY
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val selectedCal = Calendar.getInstance().apply { time = selectedDate!! }
        if (selectedCal.before(today)) {
            Toast.makeText(requireContext(), "Ngày chiếu phải từ hôm nay trở đi", Toast.LENGTH_SHORT).show()
            return
        }

        val startTimeFull = mergeDateAndTime(selectedDate!!, selectedStartTime!!)
        val endTimeFull = mergeDateAndTime(selectedDate!!, selectedEndTime!!)

        // KIỂM TRA TRÙNG GIỜ
        lifecycleScope.launch {
            val existingShowtimes = viewModel.getShowtimesByRoomAndDateExcludeId(roomId, selectedDate!!, showtimeId)
            val isOverlap = existingShowtimes.any { showtime ->
                val existingStart = showtime.startTime
                val existingEnd = showtime.endTime
                startTimeFull.before(existingEnd) && endTimeFull.after(existingStart)
            }
            if (isOverlap) {
                Toast.makeText(requireContext(), "Đã có suất chiếu trùng khung giờ!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val updatedShowtime = viewModel.showtimeDetail.value?.copy(
                movieName = movieName,
                movieId = movieId,
                startTime = startTimeFull,
                endTime = endTimeFull,
                date = selectedDate,
                status = selectedStatus,
                screenType = selectedScreenType,
                screenCategory = selectedScreenCategory,
                price = price,
                updatedAt = Date(),
                roomId = roomId,
                roomName = roomName,
                districtName = districtName,
                totalSeat = totalSeat,
                seatInEachRow = seatInEachRow
            )

            if (updatedShowtime != null) {
                viewModel.updateShowtime(showtimeId, updatedShowtime)
            }
        }
    }


    private fun showDatePicker() {
        val now = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
            binding.btnSelectDate.text = dateFullFormat.format(selectedDate!!)
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
                binding.btnStartTime.text = dateFormat.format(time)
            } else {
                selectedEndTime = time
                binding.btnEndTime.text = dateFormat.format(time)
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
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

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
    }
}
