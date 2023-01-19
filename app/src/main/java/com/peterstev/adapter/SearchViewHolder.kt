package com.peterstev.adapter

import androidx.recyclerview.widget.RecyclerView
import com.peterstev.R
import com.peterstev.databinding.ItemSearchBinding
import com.peterstev.domain.model.geolocation.GeoLocation

class SearchViewHolder(
    private val binding: ItemSearchBinding,
    private val listener: LocationItemListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GeoLocation) {
        binding.itemCity.text = binding.root.context.getString(
            R.string.location_city,
            item.city,
            item.state,
            item.country
        )

        binding.root.setOnClickListener { listener.onItemSelected(item) }
    }
}
