package com.hom.pharmacy

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hom.pharmacy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var currentTown: String
    private lateinit var adapterTown: ArrayAdapter<String>
    private lateinit var currentCounty: String
    private lateinit var viewModel: PharmacyViewModel
    private lateinit var adapterCounty: ArrayAdapter<String>
    private lateinit var spinnerTown: Spinner
    private lateinit var spinnerCounty: Spinner
    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var recy: RecyclerView
    private var getAllCountiesName: List<String>? = null
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
        progressBar.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this).get(PharmacyViewModel::class.java)
        viewModel.vmPharmaciesData()    // 下載口罩資料
        viewModel.getAllCountiesName.observe(this) {
            setSpinnerCounty(it)    // 監聽 County
        }
        viewModel.getAllTownName.observe(this) {
            setSpinnerTown(it)      // 監聽 Town
            progressBar.visibility = View.GONE
        }

    }

    fun setSpinnerCounty(countyName: List<String>) {
        adapterCounty = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            countyName
        ).apply {
            setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        spinnerCounty.adapter = adapterCounty
        spinnerCounty.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentCounty = spinnerCounty.selectedItem.toString()
                viewModel.vmUpdateTown(currentCounty)
                Log.d(TAG, "onItemSelected: mask- currentCounty- ${currentCounty}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun setSpinnerTown(townName: List<String>) {
        adapterTown = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            townName
        ).apply {
            setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        spinnerTown.adapter = adapterTown
        spinnerTown.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentTown = spinnerTown.selectedItem.toString()
                    Log.d(TAG, "onItemSelected: mask- currentTown- ${currentTown}")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    private fun findViews() {
        progressBar = binding.progressBar
        spinnerCounty = binding.spinnerCounty
        spinnerTown = binding.spinnerTown
        recy = binding.recyclerView
        recy.setHasFixedSize(true)
        recy.layoutManager = GridLayoutManager(this, 1)
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

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }


}
















