package com.example.admin.ui.features.mainmovie.show

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.data.firebase.model.FirestoreMovie
import com.example.admin.databinding.ItemFirebaseMovieBinding
import com.example.admin.databinding.ItemMovieBinding

class ShowMovieAdapter(
    private var movies: List<FirestoreMovie>,
    private val onItemClick: ((FirestoreMovie) -> Unit)? = null
) : RecyclerView.Adapter<ShowMovieAdapter.MovieViewHolder>() {

    fun updateData(newMovies: List<FirestoreMovie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val binding: ItemFirebaseMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: FirestoreMovie) {
            binding.txtNameMovie.text = movie.title
            binding.txtTime.text = movie.title
            Glide.with(binding.root.context).load(movie.posterPath).into(binding.imageView)
            binding.root.setOnClickListener {
                onItemClick?.invoke(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemFirebaseMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

}
