package com.example.covidapp.view

import android.content.res.Configuration
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.covidapp.databinding.FragmentCovidDetailsBinding
import com.example.covidapp.viewmodel.CovidDetailsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


@AndroidEntryPoint
class CovidDetailsFragment : Fragment() {

    private var _binding: FragmentCovidDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CovidDetailsViewModel by viewModels()
    private val args: CovidDetailsFragmentArgs by navArgs()
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCovidDetailsBinding.inflate(inflater, container, false)
        binding.tvCountryName.text = args.countryName

        //Status Bar
        val window = requireActivity().window

        // Before Android 6.0 Marshmallow
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Status bar will be white
            window.statusBarColor = Color.WHITE
        } else {
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                // Light mode
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.WHITE
            } else {
                // Dark mode
                window.decorView.systemUiVisibility = 0
                window.statusBarColor = Color.BLACK
            }
        }

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.confirmed.observe(viewLifecycleOwner) { confirmed ->
            viewModel.death.observe(viewLifecycleOwner) { death ->
                viewModel.recovered.observe(viewLifecycleOwner) { recovered ->
                    setupPieChart()
                    loadPieChartData()
                }
            }
        }

        viewModel.countryName.observe(viewLifecycleOwner) {
            createMap()
        }
    }

    private fun setupPieChart() {
        binding.pieChart.setDrawHoleEnabled(true)
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.setEntryLabelTextSize(12F)
        binding.pieChart.setEntryLabelColor(Color.BLACK)
        binding.pieChart.getDescription().setEnabled(false)

        val l: Legend = binding.pieChart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true
    }

    private fun loadPieChartData() {
        val confirmed = args.totalConfirmed.toFloat()
        val deaths = args.totalDeaths.toFloat()
        val recovered = 10000F

        val pieEntries = mutableListOf<PieEntry>()
        pieEntries.add(PieEntry(confirmed, "Confirmed"))
        pieEntries.add(PieEntry(deaths, "Deaths"))
        pieEntries.add(PieEntry(recovered, "Recovered"))

        val pieDataSet = PieDataSet(pieEntries, "COVID-19")
        val colors: ArrayList<Int> = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }

        pieDataSet.colors = colors
        val data = PieData(pieDataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(binding.pieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)
        binding.pieChart.setData(data)
        binding.pieChart.invalidate()
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun createMap() {
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true

            // Show which country is selected
            val country = args.countryName
            val geocoder = Geocoder(requireContext())
            try {
                val addressList = geocoder.getFromLocationName(country, 1)
                if (addressList.isNotEmpty()) {
                    val latLng = LatLng(addressList[0].latitude, addressList[0].longitude)
                    googleMap.addMarker(MarkerOptions().position(latLng))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}