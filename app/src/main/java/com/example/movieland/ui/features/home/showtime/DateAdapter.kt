package com.example.movieland.ui.features.home.showtime

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.R
import com.example.movieland.databinding.ItemDateBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class DateAdapter(
    private val dates: List<LocalDate>,
    private val onDateClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = 0

    inner class DateViewHolder(val binding: ItemDateBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(date: LocalDate, isSelected: Boolean) {
            binding.txtDate.text = date.format(DateTimeFormatter.ofPattern("dd"))
            binding.txtDay.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

            binding.root.setBackgroundResource(
                if (isSelected) R.drawable.bg_selected else R.drawable.bg_unselected
            )

            binding.root.setOnClickListener {
                val previous = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onDateClick(date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position], position == selectedPosition)
    }

    override fun getItemCount() = dates.size
}
