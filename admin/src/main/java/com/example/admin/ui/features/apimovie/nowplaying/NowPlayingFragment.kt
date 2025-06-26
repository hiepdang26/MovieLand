package com.example.admin.ui.features.apimovie.nowplaying

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.BuildConfig
import com.example.admin.R
import com.example.admin.databinding.FragmentNowPlayingBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.apimovie.detail.DetailMovieFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NowPlayingFragment : BaseFragment<FragmentNowPlayingBinding>() {
    private val accessToken: String = "BuildConfig.ACCESS_TOKEN"
    private lateinit var nowPlayingMovieAdapter: NowPlayingMovieAdapter
    private val viewModel: NowPlayingViewModel by viewModels()
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNowPlayingBinding {
        return FragmentNowPlayingBinding.inflate(layoutInflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getNowPlayingMovie(accessToken)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun setupInitialData() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    override fun setupObserver() {
        viewModel.nowPlayingMovieResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                val movies = it.results ?: emptyList()
                nowPlayingMovieAdapter = NowPlayingMovieAdapter(movies) { movieId ->
                    val detailFragment = DetailMovieFragment()
                    val bundle = Bundle().apply {
                        putString("from_fragment", "now_playing")
                        putInt("movie_id", movieId)
                    }
                    detailFragment.arguments = bundle

                    requireActivity().supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                        .replace(R.id.fragmentContainerView, detailFragment)
                        .addToBackStack(null)
                        .commit()

                }
                binding.rcvNowplayingMovie.adapter = nowPlayingMovieAdapter

                binding.rcvNowplayingMovie.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = nowPlayingMovieAdapter
                }
            }
        }

        viewModel.nowPlayingMovieResponseError.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setupClickView() {
    }
}