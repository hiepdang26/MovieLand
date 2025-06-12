package com.example.movieland.ui.features.home.showtime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.data.firebase.model.showtime.FirestoreShowtime
import com.example.movieland.databinding.ItemShowtimeBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ShowtimeAdapter : RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder>() {

    private var showtimes = listOf<FirestoreShowtime>()

    fun submitList(data: List<FirestoreShowtime>) {
        showtimes = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowtimeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemShowtimeBinding.inflate(inflater, parent, false)
        return ShowtimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowtimeViewHolder, position: Int) {
        holder.bind(showtimes[position])
    }

    override fun getItemCount(): Int = showtimes.size

    inner class ShowtimeViewHolder(private val binding: ItemShowtimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
            private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            fun bind(showtime: FirestoreShowtime) {
                val start = showtime.startTime?.let { dateFormat.format(it) } ?: "--:--"
                val end = showtime.endTime?.let { dateFormat.format(it) } ?: "--:--"
                binding.txtTime.text = "$start - $end"
                binding.txtDate.text = showtime.date?.toString() ?: ""
                binding.txtStatus.text = showtime.status

            binding.txtDate.text = showtime.startTime?.toString()
        }
    }
}
