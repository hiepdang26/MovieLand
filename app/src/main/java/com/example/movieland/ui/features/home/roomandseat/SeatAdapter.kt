package com.example.movieland.ui.features.home.roomandseat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.example.movieland.R
import com.example.movieland.ui.features.home.roomandseat.model.Seat
import com.example.movieland.ui.features.home.roomandseat.model.Seat.SeatType

class SeatAdapter(
    private var seats: List<Seat>,
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
        holder.bind(seat) {
            Log.d("SeatAdapter", "Binding seat ${seat.label} at $position isSelected=${seat.isSelected}")
            onSeatClicked(seat, position)
        }
    }

    fun updateSeats(newSeats: List<Seat>) {
        seats = newSeats
        notifyDataSetChanged()
    }
    inner class SeatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtSeatLabel = itemView.findViewById<TextView>(R.id.txtSeatLabel)

        fun bind(seat: Seat, onClick: () -> Unit) {
            txtSeatLabel.text = seat.label

            when (seat.type) {
                SeatType.NORMAL -> {
                    itemView.setBackgroundResource(
                        if (seat.isSelected) R.drawable.bg_seat_selected
                        else R.drawable.bg_seat_normal_selected
                    )
                }
                SeatType.VIP -> {
                    itemView.setBackgroundResource(
                        if (seat.isSelected) R.drawable.bg_seat_selected
                        else R.drawable.bg_seat_vip
                    )
                }
            }

            itemView.setOnClickListener {
                Log.d("SeatAdapter", "Seat clicked: ${seat.label}")
                onClick()
            }
        }

    }

}

