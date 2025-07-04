package com.hom.pharmacy

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private val TAG: String? = MapsActivity::class.java.simpleName
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    var latlngMarker: Marker? = null
    private lateinit var mContext: Context
    private var pharmacyInfo: XXXPharmacyInfo? = null
    private val requestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            it[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            setMyLocation()
//            getPharmacyData()
        } else {
            Snackbar.make(binding.root, "Loss Location Permission", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getGPSUpdateMap()
        setMyLocation()
    }

    private fun setMyLocation() {
        if (checkGPSLocationPermission()) return    // 確認 GPS Location 權限
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMyLocationButtonClickListener {
            getGPSUpdateMap()
            true
        }
        getGPSUpdateMap()   //
        getPharmacyData()   // Gson load Data
        mMap.setInfoWindowAdapter(MyInfoAdapter(mContext))  // 設定自訂義的資訊視窗樣式
        mMap.setOnInfoWindowClickListener(this)     // 設定資訊視窗點擊監聽器
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
            requestLauncher.launch(   // 請求權限
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return true
        }
        return false
    }

    private fun getGPSUpdateMap() {
        if (checkGPSLocationPermission()) return
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                latlngMarker?.remove()
                val latlng = LatLng(it.latitude, it.longitude)
                latlngMarker = mMap.addMarker(MarkerOptions().position(latlng))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 11f))
            }
        }
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
                        LatLng(it.geometry.coordinates[1], it.geometry.coordinates[0])
                    )
                    .title(it.properties.name)
                    .snippet("成人:${it.properties.mask_adult} , 兒童:${it.properties.mask_child}")
            )
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        marker.title?.also {
            val filterData = pharmacyInfo?.features?.filter {
                it.properties.name == marker.title
            }
            if (!filterData.isNullOrEmpty()) {
                Intent(this, PharmacyDetail::class.java)
                    .apply { putExtra("DATA", filterData.first()) }
                    .also { startActivity(it) }
            }
        } ?: Log.d(TAG, "onInfoWindowClick: mask- 查無資料")
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








