package com.peterstev.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peterstev.R
import com.peterstev.adapter.WeatherAdapter
import com.peterstev.databinding.DefaultFragmentViewBinding
import com.peterstev.domain.model.weather.WeatherItem
import com.peterstev.injection.AppComponent
import com.peterstev.util.daggerParentFragmentViewModel
import com.peterstev.viewmodel.WeatherViewModel

class TodayFragment : Fragment(R.layout.default_fragment_view) {

    private lateinit var component: AppComponent
    private val viewModel by daggerParentFragmentViewModel { component.sharedViewModel }

    private lateinit var binding: DefaultFragmentViewBinding
    private lateinit var weatherAdapter: WeatherAdapter
    private var weatherList = mutableListOf<WeatherItem>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component = AppComponent.create(this)
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return if (::binding.isInitialized) binding.root else {
            binding = DefaultFragmentViewBinding.inflate(layoutInflater)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeTodayWeather()
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            defaultRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            defaultRecyclerview.setHasFixedSize(true)
            weatherAdapter = WeatherAdapter(weatherList)
            defaultRecyclerview.adapter = weatherAdapter
        }
    }

    private fun observeTodayWeather() {
        viewModel.weatherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is WeatherViewModel.WeatherState.RemoteData ->
                    updateWeather(it.data.hourly)
                is WeatherViewModel.WeatherState.LocalData ->
                    updateWeather(it.data.hourly)
                else -> Unit
            }
        }
    }

    private fun updateWeather(list: MutableList<WeatherItem>) {
        weatherList.clear()
        weatherList.addAll(list)
        weatherAdapter.refreshData(list)
    }
}
