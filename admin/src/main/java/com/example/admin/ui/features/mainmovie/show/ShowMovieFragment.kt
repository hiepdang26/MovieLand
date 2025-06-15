package com.example.admin.ui.features.mainmovie.show

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.databinding.FragmentShowMovieBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.apimovie.ApiMovieFragment
import com.example.admin.ui.features.mainmovie.add.AddRawMovieFragment
import com.example.admin.ui.features.mainmovie.edit.EditMovieFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowMovieFragment : BaseFragment<FragmentShowMovieBinding>() {

    private val viewModel: ShowMovieViewModel by viewModels()
    private lateinit var movieAdapter: ShowMovieAdapter

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentShowMovieBinding {
        return FragmentShowMovieBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickView()
        setupInitialData()
        setupRecyclerView()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).showNavigationBar()
    }

    override fun setupClickView() {
        binding.btnAddMovie.setOnClickListener {
            binding.btnAddMovie.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Chọn cách thêm phim")
                    .setItems(arrayOf("Thêm thủ công", "Thêm từ API")) { dialog, which ->
                        when (which) {
                            0 -> parentFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, AddRawMovieFragment())
                                .addToBackStack(null)
                                .commit()
                            1 -> parentFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, ApiMovieFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                    .show()
            }
        }

    }

    override fun setupInitialData() {
        viewModel.fetchMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = ShowMovieAdapter(emptyList()) { movie ->
            navigateToEditMovie(movie.id.toString())
        }

        binding.rcvMovie.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.movies.collect { movies ->
                movieAdapter.updateData(movies)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collect { error ->
                error?.let {
                    Log.e("ShowMovieFragment", "Lỗi khi load phim: $it")
                }
            }
        }
    }

    private fun navigateToEditMovie(movieId: String) {
        val fragment = EditMovieFragment().apply {
            arguments = Bundle().apply {
                putString("movieId", movieId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}
