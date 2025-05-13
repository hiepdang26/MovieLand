package com.example.admin.ui.features.apimovie.nowplaying

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.data.model.apimovie.NowPlayingMovieResponse
import com.example.admin.databinding.ItemMovieBinding
class NowPlayingMovieAdapter(
    private val movies: List<NowPlayingMovieResponse.Result>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<NowPlayingMovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: NowPlayingMovieResponse.Result) {
            binding.txtNameMovie.text = movie.original_title
            binding.txtTime.text = "Khởi chiếu: ${movie.release_date}"

            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"
            Glide.with(binding.imageView.context)
                .load(imageUrl)
                .into(binding.imageView)

            binding.root.setOnClickListener {
                onItemClick(movie.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size
}
