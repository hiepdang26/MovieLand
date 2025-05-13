package com.example.movieland.ui.features.home.movie.show

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieland.R
import com.example.movieland.databinding.FragmentShowMovieBinding
import com.example.movieland.ui.bases.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowMovieFragment : BaseFragment<FragmentShowMovieBinding>() {

    private val viewModel: ShowMovieViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentShowMovieBinding {
        return FragmentShowMovieBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcvMovie.layoutManager = LinearLayoutManager(requireContext())



//        lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.movies.collect { movieList ->    adapter = MovieAdapter(movieList)
//                    binding.rcvMovie.adapter = adapter }
//            }
//        }
    }
}
