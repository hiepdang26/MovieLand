package com.example.admin.ui.features.room.layoutseat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.ui.features.room.layoutseat.model.Seat
import com.example.admin.ui.features.room.layoutseat.model.Seat.SeatRow

class SeatRowAdapter(
    private val rows: List<SeatRow>,
    private val onSeatClicked: (seat: Seat, positionInRow: Int, rowIndex: Int) -> Unit
) : RecyclerView.Adapter<SeatRowAdapter.SeatRowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatRowViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_seat_row, parent, false)
        return SeatRowViewHolder(view)
    }

    override fun getItemCount(): Int = rows.size

    override fun onBindViewHolder(holder: SeatRowViewHolder, position: Int) {
        holder.bind(rows[position], position)
    }

    inner class SeatRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recyclerViewSeats =
            itemView.findViewById<RecyclerView>(R.id.recyclerViewSeatsInRow)

        fun bind(seatRow: SeatRow, rowIndex: Int) {
            recyclerViewSeats.layoutManager =
                LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            recyclerViewSeats.adapter = SeatAdapter(seatRow.seats) { seat, positionInRow ->
                onSeatClicked(seat, positionInRow, rowIndex)
            }
        }
    }
}

