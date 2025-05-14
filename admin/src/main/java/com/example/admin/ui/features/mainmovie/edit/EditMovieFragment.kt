package com.example.admin.ui.features.mainmovie.edit

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.data.firebase.model.FirestoreMovie
import com.example.admin.databinding.FragmentAddRawMovieBinding
import com.example.admin.databinding.FragmentEditMovieBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.mainmovie.add.AddRawMovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import kotlin.getValue
@AndroidEntryPoint
class EditMovieFragment : BaseFragment<FragmentEditMovieBinding>() {

    private val viewModel: EditMovieViewModel by viewModels()
    private var selectedPosterUri: Uri? = null
    private var movieId: String? = null
    private var originalMovie: FirestoreMovie? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedPosterUri = it
                binding.imgPoster.setImageURI(it)
            }
        }

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentEditMovieBinding {
        return FragmentEditMovieBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movieId = arguments?.getString("movieId")
        movieId?.let { viewModel.loadMovieDetail(it) }

        setupObserver()
        setupClickView()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
    }

    private fun setupObserver() {
        viewModel.movieDetail.observe(viewLifecycleOwner) { movie ->
            originalMovie = movie
            binding.edtTitle.setText(movie.title)
            binding.edtOverview.setText(movie.overview)
            binding.edtRuntime.setText(movie.runtime.toString())
            binding.txtReleaseDate.setText(movie.releaseDate)
            binding.edtGenres.setText(movie.genres.joinToString(", "))
            binding.edtTrailerKey.setText(movie.trailerKey)
            binding.cbAdult.isChecked = movie.adult

            Glide.with(requireContext())
                .load(movie.posterPath)
                .into(binding.imgPoster)
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "✅ Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "❌ Lỗi cập nhật: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "✅ Đã xóa phim", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Quay lại màn trước (ShowMovie)
            }.onFailure {
                Toast.makeText(requireContext(), "❌ Xóa thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickView() {
        binding.btnChooseDate.setOnClickListener {
            showDatePicker()
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnChoosePoster.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Xóa phim")
                .setMessage("Bạn có chắc chắn muốn xóa phim này không?")
                .setPositiveButton("Xóa") { _, _ ->
                    movieId?.let { viewModel.deleteMovie(it) }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
        binding.btnUpdateMovie.setOnClickListener {
            val updatedFields = mutableMapOf<String, Any>()
            val current = originalMovie ?: return@setOnClickListener

            fun updateIfChanged(field: String, oldValue: Any?, newValue: Any?) {
                if (oldValue != newValue && newValue != null) {
                    updatedFields[field] = newValue
                }
            }

            updateIfChanged("title", current.title, binding.edtTitle.text.toString().trim())
            updateIfChanged("overview", current.overview, binding.edtOverview.text.toString().trim())
            updateIfChanged("runtime", current.runtime, binding.edtRuntime.text.toString().toIntOrNull())
            updateIfChanged("releaseDate", current.releaseDate, binding.txtReleaseDate.text.toString().trim())
            updateIfChanged("genres", current.genres, binding.edtGenres.text.toString().split(",").map { it.trim() })
            updateIfChanged("trailerKey", current.trailerKey, binding.edtTrailerKey.text.toString().trim())
            updateIfChanged("adult", current.adult, binding.cbAdult.isChecked)

            if (updatedFields.isEmpty() && selectedPosterUri == null) {
                Toast.makeText(requireContext(), "Không có thay đổi nào để lưu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateMovie(movieId!!, updatedFields, selectedPosterUri)
        }
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val dateString = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            binding.txtReleaseDate.text = dateString
        }, year, month, day)

        datePickerDialog.show()
    }
}
