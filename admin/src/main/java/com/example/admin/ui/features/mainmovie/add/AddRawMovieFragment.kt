package com.example.admin.ui.features.mainmovie.add

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
@AndroidEntryPoint
class AddRawMovieFragment : BaseFragment<FragmentAddRawMovieBinding>() {

    private val viewModel: AddRawMovieViewModel by viewModels()
    private var selectedPosterUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedPosterUri = it
                // hiển thị ảnh chọn tạm thời
                binding.imgPoster.setImageURI(it)
            }
        }

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentAddRawMovieBinding {
        return FragmentAddRawMovieBinding.inflate(inflater, container, false)
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

    private fun setupObserver() {
        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "🎉 Thêm phim thành công!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "❌ Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.uploadedPosterUrl.observe(viewLifecycleOwner) { url ->
            if (!url.isNullOrEmpty()) {
                // Hiển thị ảnh từ Cloudinary
                Glide.with(requireContext())
                    .load(url)
                    .into(binding.imgPoster)
            }
        }
    }

    private fun setupClickView() {
        binding.btnChoosePoster.setOnClickListener {
            if (!PermissionHelper.hasStoragePermission(requireActivity())) {
                PermissionHelper.requestStoragePermission(requireActivity())
            } else {
                pickImageLauncher.launch("image/*")
            }
        }

        binding.btnAddMovie.setOnClickListener {
            val title = binding.edtTitle.text.toString().trim()
            val overview = binding.edtOverview.text.toString().trim()
            val runtime = binding.edtRuntime.text.toString().toIntOrNull() ?: 0
            val releaseDate = binding.edtReleaseDate.text.toString().trim()
            val voteAverage = binding.edtVoteAverage.text.toString().toDoubleOrNull() ?: 0.0
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
                    id = (System.currentTimeMillis() / 1000).toInt(),
                    title = title,
                    overview = overview,
                    runtime = runtime,
                    releaseDate = releaseDate,
                    voteAverage = voteAverage,
                    genres = genres,
                    trailerKey = trailerKey,
                    adult = adult
                )
            )
        }
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
