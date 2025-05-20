import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.firebase.model.region.FirestoreRegion
import com.example.admin.databinding.ItemRegionBinding

class RegionAdapter : ListAdapter<FirestoreRegion, RegionAdapter.RegionViewHolder>(DiffCallback()) {

    var onItemClick: ((FirestoreRegion) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val region = getItem(position)
        holder.bind(region)
    }

    inner class RegionViewHolder(private val binding: ItemRegionBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(region: FirestoreRegion) {
            binding.txtRegionName.text = region.name
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FirestoreRegion>() {
        override fun areItemsTheSame(oldItem: FirestoreRegion, newItem: FirestoreRegion) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FirestoreRegion, newItem: FirestoreRegion) = oldItem == newItem
    }
}
