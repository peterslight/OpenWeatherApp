package com.peterstev.adapter

import androidx.recyclerview.widget.RecyclerView
import com.peterstev.R
import com.peterstev.data.util.IMAGE_BASE_URL
import com.peterstev.databinding.ItemWeatherBinding
import com.peterstev.domain.model.weather.WeatherItem
import com.peterstev.util.loadImage

class WeatherViewHolder(
    private val binding: ItemWeatherBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(weather: WeatherItem) {
        val item = weather.weather.first()
        item.icon.isNotEmpty().let {
            if (it) binding.itemImaqe.loadImage(IMAGE_BASE_URL(item.icon))
        }
        binding.itemTemp.text = binding.itemTemp.context.getString(R.string.temperature, weather.temp.toString())
        binding.itemCondition.text = item.description

        binding.chipHumidity.text = binding.chipHumidity.context.getString(R.string.humidity, "${weather.humidity}%")
        binding.chipFeelsLike.text = binding.chipFeelsLike.context.getString(R.string.temperature, weather.feelsLike.toString())
        binding.itemDateTime.text = weather.dateTime
        binding.chipSunrise.text = weather.sunrise
        binding.chipSunset.text = weather.sunset

    }
}
