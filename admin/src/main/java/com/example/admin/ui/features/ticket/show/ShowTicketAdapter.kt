package com.example.admin.ui.features.ticket.show

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.firebase.model.ticket.FirestoreTicket
import com.example.admin.databinding.ItemTicketBinding

class ShowTicketAdapter(
    private val items: List<FirestoreTicket>
) : RecyclerView.Adapter<ShowTicketAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FirestoreTicket) {
            binding.txtNameTicket.text = item.seatLabel

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
