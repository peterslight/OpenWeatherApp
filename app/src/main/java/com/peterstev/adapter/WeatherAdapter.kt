package com.peterstev.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.peterstev.databinding.ItemWeatherBinding
import com.peterstev.domain.model.weather.WeatherItem

class WeatherAdapter(
    private val items: MutableList<WeatherItem>,
) : RecyclerView.Adapter<WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun refreshData(list: MutableList<WeatherItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
