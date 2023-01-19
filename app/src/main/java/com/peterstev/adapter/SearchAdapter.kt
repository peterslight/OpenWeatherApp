package com.peterstev.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.peterstev.databinding.ItemSearchBinding
import com.peterstev.domain.model.geolocation.GeoLocation

class SearchAdapter(
    private val items: MutableList<GeoLocation>,
    private val listener: LocationItemListener,
) : RecyclerView.Adapter<SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun refreshData(list: MutableList<GeoLocation>) {
        val size = items.size
        items.clear()
        items.addAll(list)
        notifyItemRangeInserted(0, size)
    }

    fun reset() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }
}
