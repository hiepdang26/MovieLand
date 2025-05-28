import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.admin.databinding.FragmentAddShowtimeBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.showtimes.add.AddShowtimeViewModel
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
    private var selectedStatus: String = "pending" // mặc định "đang chờ"

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAddShowtimeBinding.inflate(inflater, container, false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun setupInitialData() {
        setupStatusSpinner()
    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.saveResult.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        val newShowtimeId = it.getOrNull() ?: ""
                        Toast.makeText(requireContext(), "Thêm suất chiếu thành công: ID = $newShowtimeId", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.resetSaveResult()
                }
            }
        }
    }

    override fun setupClickView() {
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedStatus = when (position) {
                    0 -> "active"   // Đang hoạt động
                    1 -> "pending"  // Đang chờ (mặc định)
                    2 -> "cancel"   // Hủy
                    else -> "pending"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedStatus = "pending"
            }
        }


        binding.btnSaveShowtime.setOnClickListener {
            saveShowtime()
        }
    }

    private fun setupStatusSpinner() {
        val statusList = listOf("Đang hoạt động", "Đang chờ", "Hủy")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter
        binding.spinnerStatus.setSelection(1)  // mặc định chọn "Đang chờ"
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
        // Kiểm tra hợp lệ dữ liệu
        val roomId = arguments?.getString("roomId") ?: ""
        val movieId = arguments?.getString("movieId") ?: ""
        val movieName = arguments?.getString("movieName") ?: ""

        if (roomId.isBlank() || movieId.isBlank()) {
            Toast.makeText(requireContext(), "Phòng hoặc phim không hợp lệ", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo đối tượng thời gian đầy đủ có ngày + giờ
        val startTimeFull = mergeDateAndTime(selectedDate!!, selectedStartTime!!)
        val endTimeFull = mergeDateAndTime(selectedDate!!, selectedEndTime!!)

        viewModel.addShowtime(
            roomId = roomId,
            movieId = movieId,
            movieName = movieName,
            startTime = startTimeFull,
            endTime = endTimeFull,
            date = selectedDate!!,
            status = selectedStatus
        )
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
