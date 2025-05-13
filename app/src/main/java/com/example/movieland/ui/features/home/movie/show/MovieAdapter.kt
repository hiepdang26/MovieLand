package com.example.movieland.ui.features.home.movie.show

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieland.data.firebase.model.FirestoreMovie
import com.example.movieland.databinding.ItemMovieBinding

class MovieAdapter(private val movies: List<FirestoreMovie>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: FirestoreMovie) {
            binding.txtNameMovie.text = movie.title
            binding.txtTime.text = movie.releaseDate
            Glide.with(binding.imageView.context)
                .load(movie.posterPath)
                .into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size
}
