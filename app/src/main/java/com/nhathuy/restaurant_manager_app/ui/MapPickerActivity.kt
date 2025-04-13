package com.nhathuy.restaurant_manager_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.databinding.ActivityMapPickerBinding

class MapPickerActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var binding:ActivityMapPickerBinding
    private lateinit var mMap:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLatLng: LatLng? =null
    private var selectedAddress: String = ""

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val EXTRA_SELECTED_ADDRESS = "selected_address"
        const val EXTRA_SELECTED_LAT = "selected_lat"
        const val EXTRA_SELECTED_LNG = "selected_lng"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        initializeMap()
        initializeFusedLocation()
        setupSearchView()
        setupConfirmBtn()
    }

    private fun setupToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.pick_location)
    }
    private fun initializeMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    private fun initializeFusedLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    private fun setupSearchView(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchLocation(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
    private fun setupConfirmBtn(){

    }

    override fun onMapReady(p0: GoogleMap) {

    }
    private fun searchLocation(locationName: String){

    }
}