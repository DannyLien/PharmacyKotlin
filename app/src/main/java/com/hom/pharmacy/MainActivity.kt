package com.hom.pharmacy

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hom.pharmacy.data.XXXPharmacyInfo
import com.hom.pharmacy.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var recy: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var binding: ActivityMainBinding
    private val TAG: String? = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
        getPharmaciesData()
    }

    private fun findViews() {
        progressBar = binding.progressBar
        recy = binding.recyclerView
        recy.setHasFixedSize(true)
        recy.layoutManager = GridLayoutManager(this, 1)
    }

    private fun getPharmaciesData() {
        progressBar.visibility = View.VISIBLE
//        val pharmaciesDataUrl = "http://delexons.ddns.net:81/pharmacies/info.json"
        val pharmaciesDataUrl = "http://delexons.ddns.net:81/pharmacies/info_132.json"
        val okHttpClien = OkHttpClient().newBuilder().build()
        val request = Request.Builder().url(pharmaciesDataUrl).get().build()
        val call = okHttpClien.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val pharmaciesData = response.body?.string()
//                    Log.d(TAG, "getPharmaciesData: mask- json- ${pharmaciesData}")
                val pharmacyInfo = Gson().fromJson(pharmaciesData, XXXPharmacyInfo::class.java)
                Log.d(TAG, "getPharmaciesData: mask- gson- ${pharmacyInfo}")
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    recy.adapter = MainAdapter(this@MainActivity, pharmacyInfo.features)
                }
            }
        })
//            val result = call.execute()
//            val resultBody = result.body?.string()
//            Log.d(TAG, "getPharmaciesData: mask- json- ${resultBody}")
        // gson
//        CoroutineScope(Dispatchers.IO).launch {
//            val pharmaciesData = URL(pharmaciesDataUrl).readText()
//            val pharmacyInfo = Gson().fromJson(pharmaciesData, PharmacyInfo::class.java)
//            Log.d(TAG, "getPharmaciesData: mask- gson- ${pharmacyInfo}")
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuId = item.itemId
        when (menuId) {
            R.id.action_exit -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}

















