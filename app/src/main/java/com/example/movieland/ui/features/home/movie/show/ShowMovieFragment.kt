package com.example.movieland.ui.features.home.movie.show

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.MainActivity
import com.example.movieland.R
import com.example.movieland.databinding.FragmentShowMovieBinding
import com.example.movieland.ui.features.home.movie.detail.DetailMovieFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowMovieFragment : BaseFragment<FragmentShowMovieBinding>() {

    private val viewModel: ShowMovieViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter

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
        setupSearchListener()

    }
    private fun setupSearchListener() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchText = s?.toString()?.trim() ?: ""
                viewModel.searchMovies(searchText)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).showNavigationBar()
    }

    override fun setupClickView() {

    }

    override fun setupInitialData() {
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