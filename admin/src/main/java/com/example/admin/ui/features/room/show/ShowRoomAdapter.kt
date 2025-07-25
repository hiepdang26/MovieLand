package com.example.admin.ui.features.room.show

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.firebase.model.room.FirestoreRoom
import com.example.admin.databinding.ItemRoomBinding

class ShowRoomAdapter(
    private val onRoomClick: (FirestoreRoom) -> Unit
) : RecyclerView.Adapter<ShowRoomAdapter.RoomViewHolder>() {

    private val rooms = mutableListOf<FirestoreRoom>()

    fun submitList(newRooms: List<FirestoreRoom>) {
        rooms.clear()
        rooms.addAll(newRooms)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int = rooms.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    inner class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(room: FirestoreRoom) {
            binding.txtNameRoom.text = room.name
            binding.txtSeat.text = "Tổng số ghế: ${room.totalSeats}"

            binding.root.setOnClickListener {
                onRoomClick(room)
            }
        }
    }
}

