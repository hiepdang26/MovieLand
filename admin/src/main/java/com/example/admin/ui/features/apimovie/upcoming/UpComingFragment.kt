package com.example.admin.ui.features.apimovie.upcoming

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.BuildConfig
import com.example.admin.R
import com.example.admin.databinding.FragmentUpComingBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.apimovie.detail.DetailMovieFragment
import com.example.admin.ui.features.apimovie.nowplaying.NowPlayingMovieAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpComingFragment : BaseFragment<FragmentUpComingBinding>() {

    private val accessToken: String = "BuildConfig.ACCESS_TOKEN"
    private lateinit var upComingMovieAdapter: UpComingMovieAdapter
    private val viewModel: UpComingViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpComingBinding {
        return FragmentUpComingBinding.inflate(inflater, container, false)
    }

    override fun setupInitialData() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUpComingMovie(accessToken)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }



    override fun setupObserver() {
        viewModel.upComingMovieResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                val movies = it.results ?: emptyList()
                upComingMovieAdapter = UpComingMovieAdapter(movies) { movieId ->
                    val detailFragment = DetailMovieFragment()
                    val bundle = Bundle().apply {
                        putString("from_fragment", "upcoming")
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
                binding.rcvUpcomingMovie.adapter = upComingMovieAdapter

                binding.rcvUpcomingMovie.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = upComingMovieAdapter
                }
            }
        }

        viewModel.upComingMovieResponseError.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setupClickView() {
        TODO("Not yet implemented")
    }
}
