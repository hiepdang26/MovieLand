package com.example.admin.ui.features.room.layoutseat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.ui.features.room.layoutseat.model.Seat
import com.example.admin.ui.features.room.layoutseat.model.Seat.SeatType
import android.util.Log

class SeatAdapter(
    private val seats: List<Seat>,
    private val onSeatClicked: (seat: Seat, position: Int) -> Unit
) : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    private val TAG = "SeatAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seat, parent, false)
        return SeatViewHolder(view)
    }

    override fun getItemCount(): Int = seats.size

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = seats[position]
        holder.bind(seat)
    }

    inner class SeatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtSeatLabel = itemView.findViewById<TextView>(R.id.txtSeatLabel)

        fun bind(seat: Seat) {
            txtSeatLabel.text = seat.label
            val bgRes = when {
                seat.isSelected && seat.type == SeatType.NORMAL -> R.drawable.bg_seat_normal_selected
                seat.isSelected && seat.type == SeatType.VIP -> R.drawable.bg_seat_vip_selected
                !seat.isSelected && seat.type == SeatType.NORMAL -> R.drawable.bg_seat_normal
                !seat.isSelected && seat.type == SeatType.VIP -> R.drawable.bg_seat_vip
                else -> R.drawable.bg_seat_normal
            }
            txtSeatLabel.setBackgroundResource(bgRes)

            txtSeatLabel.setOnClickListener {
                onSeatClicked(seat, adapterPosition)
            }
        }
    }
}

