package com.peterstev.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.peterstev.R
import com.peterstev.adapter.FragmentPagerAdapter
import com.peterstev.adapter.LocationItemListener
import com.peterstev.adapter.SearchAdapter
import com.peterstev.data.util.IMAGE_BASE_URL
import com.peterstev.databinding.FragmentMainBinding
import com.peterstev.databinding.ItemErrorBinding
import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherItem
import com.peterstev.injection.AppComponent
import com.peterstev.util.*
import com.peterstev.viewmodel.WeatherViewModel
import io.nlopez.smartlocation.SmartLocation


class MainFragment : Fragment(R.layout.fragment_main), LocationItemListener {

    private lateinit var component: AppComponent
    private val viewModel by daggerParentFragmentViewModel { component.sharedViewModel }
    private lateinit var binding: FragmentMainBinding
    private lateinit var errorBinding: ItemErrorBinding
    private lateinit var searchAdapter: SearchAdapter
    private var locationList = mutableListOf<GeoLocation>()
    private lateinit var geoLocation: GeoLocation
    private lateinit var permission: ActivityResultLauncher<String>
    private lateinit var bottomSheet: BottomSheetDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component = AppComponent.create(this)
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return if (::binding.isInitialized) binding.root else {
            binding = FragmentMainBinding.inflate(layoutInflater)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorBinding = ItemErrorBinding.inflate(layoutInflater)
        permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) getLocation()
            else onError(getString(R.string.no_location))
        }

        viewModel.initialize()
        observeLocationUpdates()

        setupBackPressedListeners()
        observeLocationData()
        observeTodayWeather()
        setupViews()
        getLocation()
    }

    private fun shouldRequestPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        if (shouldRequestPermission()) {
            permission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else SmartLocation
            .with(requireContext())
            .location()
            .oneFix()
            .start { location ->
                location?.let {
                    SmartLocation.with(requireContext())
                        .geocoding().reverse(location) { location, addressList ->
                            if (addressList.isNotEmpty()) {
                                addressList.first()
                                    .let {
                                        it.getAddressLine(0)?.let { addressLine ->
                                            val section = addressLine.split(", ")
                                            val geo = GeoLocation(
                                                country = it.countryName,
                                                latitude = it.latitude,
                                                longitude = it.longitude,
                                                city = section[0],
                                                state = section[section.size - 2]
                                            )
                                            updateLocation(geo)
                                        }
                                    }
                            }
                        }
                }
            }
    }

    private fun setupViews() {
        with(binding) {
            searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            searchRecyclerView.setHasFixedSize(true)
            searchAdapter = SearchAdapter(locationList, this@MainFragment)
            searchRecyclerView.adapter = searchAdapter

            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    cleanUpSearch()
                    val text = searchView.text.toString()
                    when {
                        networkIsAvailable() && text.trim().isNotEmpty() -> {
                            searchBar.text = text
                            hideKeyboard()
                            cleanUpSearch()
                            viewModel.searchCity(text.trim())
                            true
                        }
                        text.trim().isEmpty() -> {
                            Toast.makeText(requireContext(), getString(R.string.enter_text_to_search), Toast.LENGTH_SHORT).show()
                            true
                        }
                        else -> {
                            onEmptySearchResult(true, getString(R.string.no_internet), true)
                            true
                        }
                    }
                }

            tablayout.tabGravity = TabLayout.GRAVITY_FILL
            val pagerAdapter = FragmentPagerAdapter(this@MainFragment)
            viewPager.adapter = pagerAdapter
            viewPager.offscreenPageLimit = 2

            TabLayoutMediator(tablayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.today)
                    1 -> getString(R.string.tomorrow)
                    else -> getString(R.string.later)
                }
            }.attach()

            currentLocation.setOnClickListener {
                getLocation()
                Toast.makeText(requireContext(), getString(R.string.refresh_location), Toast.LENGTH_SHORT).show()
            }
        }
        initSheet()
    }

    private fun updateHeader(item: WeatherItem) {
        with(binding.weatherHeader) {
            location.text = getString(
                R.string.location,
                "${geoLocation.city},${geoLocation.state}",
                geoLocation.country
            )
            tvTemp.text = getString(R.string.temperature, item.temp.toString())
            tvCondition.text = item.weather.firstOrNull()?.description ?: ""
            imgCondition.loadImage(IMAGE_BASE_URL(item.weather.firstOrNull()?.icon ?: ""))
            chipFeelsLike.text = getString(R.string.temperature, item.feelsLike.toString())
            chipSunrise.text = item.sunrise
            chipSunset.text = item.sunset
            chipCurrentTime.text = item.dateTime
            chipHumidity.text = getString(R.string.humidity, "${item.humidity}%")
            imgReload.setOnClickListener {
                updateLocation(geoLocation)
                Toast.makeText(requireContext(), getString(R.string.refresh_data), Toast.LENGTH_SHORT).show()
            }
            root.show()
        }
    }

    private fun observeLocationData() {
        viewModel.locationStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is WeatherViewModel.LocationState.Error -> {
                    val message = if (it.throwable.message.toString().contains(getString(R.string.network_error), true)
                        || it.throwable.message.toString().contains(getString(R.string.network_connect), true)
                    ) getString(R.string.no_internet) else it.throwable.message.toString()
                    onEmptySearchResult(true, message, isError = true)
                    onProgress(false)
                }
                is WeatherViewModel.LocationState.Loading -> {
                    if (it.isLoading) onEmptySearchResult(false)
                    onProgress(it.isLoading)
                }
                is WeatherViewModel.LocationState.Success -> {
                    locationList.addAll(it.data)
                    if (locationList.isEmpty()) {
                        onEmptySearchResult(true, binding.searchView.text.toString())
                        return@observe
                    }
                    searchAdapter.refreshData(it.data)
                }
            }
        }
    }

    private fun observeTodayWeather() {
        viewModel.weatherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is WeatherViewModel.WeatherState.Error -> onError(it.throwable.message.toString())
                is WeatherViewModel.WeatherState.Loading -> {
                    if (it.isLoading) {
                        bottomSheet.hide()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.fetching_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is WeatherViewModel.WeatherState.RemoteData -> {
                    viewModel.saveWeatherData(it.data)
                    updateHeader(it.data.current)
                }
                is WeatherViewModel.WeatherState.LocalData -> {
                    updateHeader(it.data.current)
                }
            }
        }
    }


    private fun initSheet() {
        bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(errorBinding.root)
    }

    private fun onError(error: String) {
        with(errorBinding) {
            btSearch.setOnClickListener {
                bottomSheet.hide()
                binding.searchView.show()
            }
            itemMessage.text = error
            if (error == getString(R.string.no_location)) {
                btPermission.show()
                btPermission.setOnClickListener {
                    bottomSheet.hide()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:" + requireContext().packageName)
                    startActivity(intent)
                }
            } else btPermission.hide()
        }
        bottomSheet.show()
    }

    private fun onEmptySearchResult(shouldShow: Boolean, message: String = "", isError: Boolean = false) {
        if (shouldShow) binding.emptySearchLayout.root.show()
        else binding.emptySearchLayout.root.hide()

        if (isError) binding.emptySearchLayout.itemMessage.text = message
        else binding.emptySearchLayout.itemMessage.text = getString(
            R.string.we_found_no_results_for_try_searching_another_city,
            message
        )
    }

    private fun onProgress(shouldShow: Boolean) {
        if (shouldShow) binding.progress.root.show()
        else binding.progress.root.hide()
    }

    private fun cleanUpSearch() {
        locationList.clear()
        searchAdapter.reset()
    }

    private fun setupBackPressedListeners() {
        val callback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() = binding.searchView.hide()
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, callback)

        binding.searchView.addTransitionListener { searchView, _, _ ->
            callback.isEnabled = searchView.isShowing
        }
    }

    override fun onItemSelected(item: GeoLocation) {
        updateLocation(item)
    }

    private fun updateLocation(item: GeoLocation) {
        viewModel.onGeoLocationUpdated(item)
        geoLocation = item
        viewModel.getWeather(geoLocation.latitude, geoLocation.longitude)
        binding.searchView.hide()
    }

    private fun observeLocationUpdates() {
        viewModel.geoLocationLiveData.observe(viewLifecycleOwner) {
            geoLocation = it
        }
    }
}
