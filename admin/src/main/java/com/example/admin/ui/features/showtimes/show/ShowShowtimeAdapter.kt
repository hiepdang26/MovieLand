package com.example.admin.ui.features.showtimes.show

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import com.example.admin.databinding.ItemShowtimeBinding
import java.text.SimpleDateFormat
import java.util.Locale


class ShowShowtimeAdapter(
    private val showtimes: List<FirestoreShowtime>,
    private val onItemClick: (String) -> Unit

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
        private val fullDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun bind(showtime: FirestoreShowtime) {
            val start = showtime.startTime?.let { dateFormat.format(it) } ?: "--:--"
            val end = showtime.endTime?.let { dateFormat.format(it) } ?: "--:--"
            binding.txtTime.text = "$start - $end"

            binding.txtDate.text = showtime.date?.let { fullDateFormat.format(it) } ?: ""

            binding.txtStatus.text = when (showtime.status) {
                "pending" -> "Đang chờ"
                "active" -> "Đang hoạt động"
                "cancel", "cancelled" -> "Đã hủy"
                else -> showtime.status
            }

            val color = when (showtime.status) {
                "pending" -> Color.YELLOW
                "active" -> Color.GREEN
                "cancel", "cancelled" -> Color.RED
                else -> Color.GRAY
            }
            binding.txtStatus.setTextColor(color)
            itemView.setOnClickListener {
                onItemClick(showtime.id)
            }
        }
    }

}