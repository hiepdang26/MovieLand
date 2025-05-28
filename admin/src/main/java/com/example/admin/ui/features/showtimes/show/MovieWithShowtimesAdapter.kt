package com.example.admin.ui.features.showtimes.show

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.ui.features.showtimes.show.model.MovieWithShowtimes

class MovieWithShowtimesAdapter(private var movies: List<MovieWithShowtimes>) : RecyclerView.Adapter<MovieWithShowtimesAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_movie_with_showtimes, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMovieName = itemView.findViewById<TextView>(R.id.tvMovieName)
        private val rvShowtimes = itemView.findViewById<RecyclerView>(R.id.rvShowtimes)

        fun bind(movieWithShowtimes: MovieWithShowtimes) {
            tvMovieName.text = movieWithShowtimes.movieName
            rvShowtimes.apply {
                layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = ShowShowtimeAdapter(movieWithShowtimes.showtimes)
                setRecycledViewPool(RecyclerView.RecycledViewPool())
            }
        }
    }
    fun submitList(newMovies: List<MovieWithShowtimes>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}
