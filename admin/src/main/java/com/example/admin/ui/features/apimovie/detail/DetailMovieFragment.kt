package com.example.admin.ui.features.apimovie.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.admin.BuildConfig
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.data.firebase.model.FirestoreMovie
import com.example.admin.databinding.FragmentDetailMovieBinding
import com.example.admin.ui.bases.BaseFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailMovieFragment : BaseFragment<FragmentDetailMovieBinding>() {
    private val accessToken: String = "BuildConfig.ACCESS_TOKEN"
    private var movieId: Int = -1
    private var fromFragment: String? = null

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
        movieId = arguments?.getInt("movie_id") ?: -1
        fromFragment = arguments?.getString("from_fragment")

        viewModel.getDetailMovie(accessToken, movieId)
        viewModel.getTrailerMovie(accessToken, movieId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        binding.progressBar.visibility= View.VISIBLE
        if (fromFragment == "upcoming") {
            binding.btnAddMovie.visibility = View.GONE
        } else {
            binding.btnAddMovie.visibility = View.VISIBLE
        }
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
            val detail = viewModel.detailMovieResponse.value
            val trailerResponse = viewModel.trailerMovieResponse.value

            if (detail != null) {
                val trailerKey = trailerResponse?.results
                    ?.firstOrNull { it.site == "YouTube" && it.type == "Trailer" && it.official }
                    ?.key
                    ?: trailerResponse?.results
                        ?.firstOrNull { it.site == "YouTube" }
                        ?.key

                val firestoreMovie = FirestoreMovie(
                    id = detail.id.toString(),
                    title = detail.title,
                    overview = detail.overview,
                    posterPath = "https://image.tmdb.org/t/p/w500${detail.poster_path}",
                    trailerKey = trailerKey ?: "",
                    runtime = detail.runtime,
                    releaseDate = detail.release_date,
                    genres = detail.genres.map { it.name },
                    adult = detail.adult
                )

                viewModel.uploadMovie(firestoreMovie)
            } else {
                Toast.makeText(requireContext(), "Không có dữ liệu phim để thêm", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun setupObserver() {
        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Thêm vào Firestore thành công", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Lỗi khi thêm: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("DetailMovieFragment", "Upload error", it)
            }
        }



        viewModel.detailMovieResponse.observe(viewLifecycleOwner) { detail ->
            binding.progressBar.visibility= View.GONE

            detail?.let {
                binding.txtTitle.text = it.title
                binding.txtOverReview.text = it.overview
                binding.txtReleaseDate.text = it.release_date
                binding.txtTime.text = "${it.runtime} phút"
                binding.txtGenre.text = it.genres.joinToString(", ") { genre -> genre.name }
                Glide.with(requireContext())
                    .load("https://image.tmdb.org/t/p/w500${it.poster_path}")
                    .into(binding.imgBackground)
            }
        }

        viewModel.detailMovieResponseError.observe(viewLifecycleOwner) { error ->
            binding.progressBar.visibility = View.GONE
            error?.let {
                Log.e("DetailMovieFragment", "Lỗi khi fetch chi tiết phim: ${it.message}", it)
            }
        }

        viewModel.trailerMovieResponse.observe(viewLifecycleOwner) { response ->
            response?.results?.let { results ->
                val trailer = results.firstOrNull {
                    it.site == "YouTube" && it.type == "Trailer" && it.official
                } ?: results.firstOrNull {
                    it.site == "YouTube"
                }

                trailer?.key?.let { key ->
                    binding.youtubePlayerView.visibility = View.VISIBLE
                    binding.imgBackground.visibility = View.GONE

                    lifecycle.addObserver(binding.youtubePlayerView)
                    binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(key, 0f)
                        }
                    })
                }
            }
        }



    }

}