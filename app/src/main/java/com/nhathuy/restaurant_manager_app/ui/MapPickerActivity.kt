package com.nhathuy.restaurant_manager_app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.databinding.ActivityMapPickerBinding
import okio.IOException
import java.util.Locale

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
        binding.btnConfirmLocation.setOnClickListener {
            if (selectedLatLng != null) {
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_SELECTED_ADDRESS, selectedAddress)
                    putExtra(EXTRA_SELECTED_LAT, selectedLatLng!!.latitude)
                    putExtra(EXTRA_SELECTED_LNG, selectedLatLng!!.longitude)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.please_select_location), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true


        mMap.setOnMapClickListener {
            latLng ->
            mMap.clear()
            selectedLatLng = latLng
            mMap.addMarker(MarkerOptions().position(latLng))

            getAddressFromLatLng(latLng)
        }

        // Check for location permission
        if (hasLocationPermission()) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }
    }
    private fun searchLocation(locationName: String){
        val geocoder = Geocoder(this, Locale.getDefault())

        //for android 13+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            geocoder.getFromLocationName(locationName, 1) { addresses ->
                handleFoundAddresses(addresses, locationName)
            }
        }
        else{
            try{
                val addressList = geocoder.getFromLocationName(locationName,1)
                handleFoundAddresses(addressList,locationName)
            }
            catch (e: IOException){
                Toast.makeText(this, getString(R.string.search_error) + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API 33+)
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                handleReverseGeocodingResult(addresses, latLng)
            }
        } else {
            // For older Android versions
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                handleReverseGeocodingResult(addresses, latLng)
            } catch (e: IOException) {
                handleGeocodeError(latLng)
            }
        }
    }

    private fun handleReverseGeocodingResult(addresses: List<Address>?, latLng: LatLng) {
        runOnUiThread {
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                selectedAddress = address.getAddressLine(0) ?: ""
                binding.tvSelectedAddress.text = selectedAddress
            } else {
                handleGeocodeError(latLng)
            }
        }
    }

    private fun handleGeocodeError(latLng: LatLng) {
        binding.tvSelectedAddress.text = getString(R.string.address_not_found)
        selectedAddress = "${latLng.latitude}, ${latLng.longitude}"
    }
    private fun handleFoundAddresses(addressList: List<Address>?, locationName: String) {
        runOnUiThread {
            if(addressList != null && addressList.isNotEmpty()){
                val address = addressList[0]
                val latLng= LatLng(address.latitude,address.longitude)

                mMap.clear()
                selectedLatLng =latLng
                mMap.addMarker(MarkerOptions().position(latLng).title(address.getAddressLine(0)))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))

                selectedAddress =address.getAddressLine(0) ?: ""

                binding.tvSelectedAddress.text = selectedAddress
            }
            else {
                Toast.makeText(this, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true

            // Get current location and move camera there
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                // Handle back button click in the toolbar
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    // For Android 13+ (API 33+)
                    finish()
                } else {
                    // For older Android versions
                    onBackPressed()
                }
                true
            }
            R.id.menu_normal_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.menu_satellite_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.menu_terrain_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.menu_hybrid_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}