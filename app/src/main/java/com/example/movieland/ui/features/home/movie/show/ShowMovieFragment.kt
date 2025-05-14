package com.example.movieland.ui.features.home.movie.show

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieland.MainActivity
import com.example.movieland.R
import com.example.movieland.databinding.FragmentShowMovieBinding
import com.example.movieland.ui.bases.BaseFragment
import com.example.movieland.ui.features.home.movie.detail.DetailMovieFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowMovieFragment : BaseFragment<FragmentShowMovieBinding>() {

    private val viewModel: ShowMovieViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter // tự tạo adapter riêng

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

    private fun setupClickView() {

    }

    private fun setupInitialData() {
        viewModel.fetchMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(emptyList()) { movie ->
            navigateToDetailMovie(movie.id.toString())
        }

        binding.rcvMovie.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    private fun setupObserver() {
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

    private fun navigateToDetailMovie(movieId: String) {
        val fragment = DetailMovieFragment().apply {
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