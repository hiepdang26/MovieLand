package com.example.admin.ui.features.showtimes.show

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import com.example.admin.databinding.ItemShowtimeBinding
import java.text.SimpleDateFormat
import java.util.Locale


class ShowShowtimeAdapter(
    private val showtimes: List<FirestoreShowtime>,
    private val onItemClick: (String) -> Unit // truyền showtimeId khi click

) : RecyclerView.Adapter<ShowShowtimeAdapter.ShowtimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowtimeViewHolder {
        val binding =
            ItemShowtimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowtimeViewHolder(binding)
    }

    override fun getItemCount(): Int = showtimes.size

    override fun onBindViewHolder(holder: ShowtimeViewHolder, position: Int) {
        holder.bind(showtimes[position])
    }

    inner class ShowtimeViewHolder(private val binding: ItemShowtimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(showtime: FirestoreShowtime) {
            val start = showtime.startTime?.let { dateFormat.format(it) } ?: "--:--"
            val end = showtime.endTime?.let { dateFormat.format(it) } ?: "--:--"
            binding.txtTime.text = "$start - $end"
            binding.txtDate.text = showtime.date?.toString() ?: ""
            binding.txtStatus.text = showtime.status

            itemView.setOnClickListener {
                onItemClick(showtime.id)
            }
        }
    }
}