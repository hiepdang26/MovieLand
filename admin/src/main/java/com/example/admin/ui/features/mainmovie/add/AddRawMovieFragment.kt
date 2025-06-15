package com.example.admin.ui.features.mainmovie.add

import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.data.firebase.model.FirestoreMovie
import com.example.admin.databinding.FragmentAddRawMovieBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AddRawMovieFragment : BaseFragment<FragmentAddRawMovieBinding>() {

    private val viewModel: AddRawMovieViewModel by viewModels()
    private var selectedPosterUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedPosterUri = it
                binding.imgPoster.setImageURI(it)
            }
        }

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentAddRawMovieBinding {
        return FragmentAddRawMovieBinding.inflate(inflater, container, false)
    }

    override fun setupInitialData() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickView()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
    }

    override fun setupObserver() {
        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "🎉 Thêm phim thành công!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "❌ Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.uploadedPosterUrl.observe(viewLifecycleOwner) { url ->
            if (!url.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(url)
                    .into(binding.imgPoster)
            }
        }
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnChoosePoster.setOnClickListener {
            if (!PermissionHelper.hasStoragePermission(requireActivity())) {
                PermissionHelper.requestStoragePermission(requireActivity())
            } else {
                pickImageLauncher.launch("image/*")
            }
        }

        binding.btnChooseDate.setOnClickListener {
            showDatePicker()
        }
        binding.btnAddMovie.setOnClickListener {
            val title = binding.edtTitle.text.toString().trim()
            val overview = binding.edtOverview.text.toString().trim()
            val runtime = binding.edtRuntime.text.toString().toIntOrNull() ?: 0
            val releaseDate = binding.txtReleaseDate.text.toString().trim()
            val genres = binding.edtGenres.text.toString().split(",").map { it.trim() }
            val trailerKey = binding.edtTrailerKey.text.toString().trim()
            val adult = binding.cbAdult.isChecked

            if (selectedPosterUri == null) {
                Toast.makeText(requireContext(), "⚠️ Vui lòng chọn ảnh poster", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.uploadPosterAndMovie(
                uri = selectedPosterUri!!,
                movie = FirestoreMovie(
                    id = (System.currentTimeMillis() / 1000).toString(),
                    title = title,
                    overview = overview,
                    runtime = runtime,
                    releaseDate = releaseDate,
                    genres = genres,
                    trailerKey = trailerKey,
                    adult = adult
                )
            )
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
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionHelper.isPermissionRequestCode(requestCode)) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(requireContext(), "Không được cấp quyền", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
