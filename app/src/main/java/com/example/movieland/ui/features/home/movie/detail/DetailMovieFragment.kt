package com.example.movieland.ui.features.home.movie.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.MainActivity
import com.example.movieland.databinding.FragmentDetailMovieBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailMovieFragment : BaseFragment<FragmentDetailMovieBinding>() {
    private var movieId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val viewModel: DetailMovieViewModel by viewModels()


    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentDetailMovieBinding {
        return FragmentDetailMovieBinding.inflate(layoutInflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun setupInitialData() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility= View.VISIBLE

        movieId = arguments?.getString("movieId")
        movieId?.let { viewModel.loadMovieDetail(it) }


        setupClickView()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()

    }
    override fun setupClickView() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnAddMovie.setOnClickListener {
        }
    }

    override fun setupObserver() {
        viewModel.movieDetail.observe(viewLifecycleOwner) { detail ->
            binding.progressBar.visibility= View.GONE

            detail?.let {
                binding.txtTitle.text = it.title
                binding.txtOverReview.text = it.overview
                binding.txtReleaseDate.text = it.releaseDate
                binding.txtTime.text = "${it.runtime} phút"
                binding.txtGenre.text = it.genres.joinToString(", ")
                Glide.with(requireContext())
                    .load("https://image.tmdb.org/t/p/w500${it.posterPath}")
                    .into(binding.imgBackground)

                if (!it.trailerKey.isNullOrEmpty()) {
                    binding.youtubePlayerView.visibility = View.VISIBLE
                    binding.imgBackground.visibility = View.GONE

                    lifecycle.addObserver(binding.youtubePlayerView)
                    binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(it.trailerKey, 0f)
                        }
                    })
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.progressBar.visibility = View.GONE
            error?.let {
                Log.e("DetailMovieFragment", "Lỗi khi fetch chi tiết phim")
            }
        }

    }

}