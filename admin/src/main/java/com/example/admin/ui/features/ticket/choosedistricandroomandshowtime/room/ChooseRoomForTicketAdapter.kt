package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.firebase.model.room.FirestoreRoom
import com.example.admin.databinding.ItemRoomBinding

class ChooseRoomForTicketAdapter(
    private val rooms: List<FirestoreRoom>,
    private val onRoomClick: (FirestoreRoom) -> Unit
) : RecyclerView.Adapter<ChooseRoomForTicketAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(private val binding: ItemRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(room: FirestoreRoom) {
            binding.txtNameRoom.text = room.name
            binding.txtSeat.text = "Tổng ghế: ${room.totalSeats}"
            binding.root.setOnClickListener {
                onRoomClick(room)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int = rooms.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(rooms[position])
    }
}

