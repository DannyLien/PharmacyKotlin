package com.hom.pharmacy

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.hom.pharmacy.MainActivity.Companion.pharmaciesDataUrl
import com.hom.pharmacy.data.XXXPharmacyInfo
import com.hom.pharmacy.databinding.ActivityMapsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private var pharmacyInfo: XXXPharmacyInfo? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val requestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            it[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            setMyLocation()
        } else {
            Snackbar.make(binding.root, "Loss Location Permission", Snackbar.LENGTH_LONG).show()
        }
    }
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var latlngMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (checkGPSLocationPermission()) return
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            latlngMarker?.remove()
            val latlng = LatLng(it.latitude, it.longitude)
            latlngMarker = mMap.addMarker(MarkerOptions().position(latlng))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))
        }
        setMyLocation()
    }

    private fun setMyLocation() {
        if (checkGPSLocationPermission()) return
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMyLocationButtonClickListener {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                latlngMarker?.remove()
                val latlng = LatLng(it.latitude, it.longitude)
                latlngMarker = mMap.addMarker(MarkerOptions().position(latlng))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
            }
            true
        }
        getPharmacyData()
    }

    private fun getPharmacyData() {
        CoroutineScope(Dispatchers.IO).launch {
            val pharmaciesData = URL(pharmaciesDataUrl).readText()
            pharmacyInfo = Gson().fromJson(pharmaciesData, XXXPharmacyInfo::class.java)
            runOnUiThread {
                addAllMaker()
            }
        }
    }

    private fun addAllMaker() {
        pharmacyInfo?.features?.forEach {
            latlngMarker = mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.geometry.coordinates[1], it.geometry.coordinates[0]
                        )
                    )
                    .title(it.properties.name)
                    .snippet("成人:${it.properties.mask_adult} , 兒童:${it.properties.mask_child}")
            )
        }
    }

    private fun checkGPSLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            getGPSLocationPermission()
            return true
        }
        return false
    }

    private fun getGPSLocationPermission() {
        requestLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pharmacy_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuId = item.itemId
        when (menuId) {
            R.id.action_detail_back -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}


















