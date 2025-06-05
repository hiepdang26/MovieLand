package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.showtime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.admin.data.firebase.model.showtime.FirestoreShowtime
import com.example.admin.databinding.ItemShowtimeBinding
import java.text.SimpleDateFormat
import java.util.*

class ChooseShowtimeForTicketAdapter(
    private val items: List<FirestoreShowtime>,
    private val onItemClick: (FirestoreShowtime) -> Unit
) : RecyclerView.Adapter<ChooseShowtimeForTicketAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemShowtimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(showtime: FirestoreShowtime) {
            val start = showtime.startTime?.let { dateFormat.format(it) } ?: "--:--"
            val end = showtime.endTime?.let { dateFormat.format(it) } ?: "--:--"
            binding.txtTime.text = "$start - $end"
            binding.txtDate.text = showtime.date?.toString() ?: ""
            binding.txtStatus.text = showtime.status

            itemView.setOnClickListener {
                onItemClick(showtime)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemShowtimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}


