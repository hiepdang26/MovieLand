package com.example.movieland.ui.features.home.roomandseat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.example.movieland.R
import com.example.movieland.ui.features.home.roomandseat.model.Seat
import com.example.movieland.ui.features.home.roomandseat.model.Seat.SeatType
import com.google.firebase.auth.FirebaseAuth

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

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            val isLockedOrBooking = seat.ticket?.status == "locked" || seat.ticket?.status == "booking"
            val isBooked = seat.ticket?.status == "booked"
            val isLockedByCurrentUser = isLockedOrBooking && seat.ticket?.userId == currentUserId
            val isSelectedLocally = seat.isSelected

            when (seat.type) {
                SeatType.NORMAL -> {
                    itemView.setBackgroundResource(
                        when {
                            isBooked -> R.drawable.bg_seat_booked
                            isSelectedLocally -> R.drawable.bg_seat_selected
                            isLockedOrBooking && !isLockedByCurrentUser -> R.drawable.bg_seat_locked
                            else -> R.drawable.bg_seat_normal_selected
                        }
                    )
                }
                SeatType.VIP -> {
                    itemView.setBackgroundResource(
                        when {
                            isBooked -> R.drawable.bg_seat_booked
                            isSelectedLocally -> R.drawable.bg_seat_selected
                            isLockedOrBooking && !isLockedByCurrentUser -> R.drawable.bg_seat_locked
                            else -> R.drawable.bg_seat_vip
                        }
                    )
                }
            }

            itemView.setOnClickListener {
                if (isLockedOrBooking && !isLockedByCurrentUser ) {
                    Log.d("SeatAdapter", "Ghế ${seat.label} đang được chọn hoặc thanh toán bởi người khác")
                    Toast.makeText(itemView.context, "Ghế này đã được chọn hoặc đang thanh toán bởi người khác", Toast.LENGTH_SHORT).show()
                }else if(isBooked){
                    Toast.makeText(itemView.context, "Ghế này đã được người khác đặt", Toast.LENGTH_SHORT).show()
                } else {
                    onClick()
                }

            }
        }

    }

}

