package tr.com.ponda.gftb.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tr.com.ponda.gftb.databinding.ListRowBinding
import tr.com.ponda.gftb.model.Term

class ListAdapter : ListAdapter<Term, MyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToViewFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Term>() {
        override fun areItemsTheSame(oldItem: Term, newItem: Term): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Term, newItem: Term): Boolean {
            return oldItem == newItem
        }
    }
}

class MyViewHolder(private val binding: ListRowBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(term: Term) {
        binding.termTxt.text = term.term
/*        binding.definitionTxt.text = term.definition
        binding.citationTxt.text = term.citation

        binding.definitionTxt.visibility = View.GONE
        binding.citationTxt.visibility = View.GONE*/
    }
}