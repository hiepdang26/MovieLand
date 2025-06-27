package com.example.movieland.ui.features.personal.ticket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.databinding.ItemBookedTicketBinding
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
class BookedTicketAdapter : RecyclerView.Adapter<BookedTicketAdapter.BookingGroupViewHolder>() {
    private var items: List<BookingGroup> = emptyList()

    fun submitList(newList: List<BookingGroup>) {
        items = newList
        notifyDataSetChanged()
    }

    inner class BookingGroupViewHolder(val binding: ItemBookedTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun formatDate(date: Date?): String {
            if (date == null) return ""
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))
            return sdf.format(date)
        }

        private fun formatCurrency(amount: Double): String {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return formatter.format(amount) + " đ"
        }

        fun bind(group: BookingGroup) {
            val firstTicket = group.tickets.firstOrNull()
            binding.txtMovieTitle.text = firstTicket?.movieName ?: ""
            binding.txtMovieDetails.text = "${formatDate(firstTicket?.startTime)} • ${firstTicket?.roomName ?: ""}"

            // Gộp các ghế
            binding.txtSeatInfo.text = group.tickets.joinToString(", ") { it.seatLabel }

            // Tổng tiền
            val total = group.tickets.sumOf { it.price }
            binding.txtPrice.text = formatCurrency(total)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingGroupViewHolder {
        val binding = ItemBookedTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingGroupViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
