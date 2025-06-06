package com.hom.pharmacy

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hom.pharmacy.data.Feature
import com.hom.pharmacy.databinding.ActivityPharmacyDetailBinding

class PharmacyDetail : AppCompatActivity() {
    private lateinit var tvAddressDet: TextView
    private lateinit var tvPhoneDet: TextView
    private lateinit var tvChildAmountDet: TextView
    private lateinit var tvAdultAmountDet: TextView
    private lateinit var tvNameDet: TextView
    private var pharmacyList: Feature? = null
    private val TAG: String? = PharmacyDetail::class.java.simpleName
    private lateinit var binding: ActivityPharmacyDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPharmacyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        pharmacyList = intent.getSerializableExtra("DATA") as? Feature
        pharmacyList?.properties?.also {
            Log.d(
                TAG, "onCreate: mask- intent- " +
                        "${it.name} , ${it.mask_adult} , ${it.mask_child} "
            )
        }
        findViews()

    }

    private fun findViews() {
        tvNameDet = binding.tvNameDet
        tvAdultAmountDet = binding.tvAdultAmountDet
        tvChildAmountDet = binding.tvChildAmountDet
        tvPhoneDet = binding.tvPhoneDet
        tvAddressDet = binding.tvAddressDet

        pharmacyList?.properties?.also {
            tvNameDet.setText(it.name)
            tvAdultAmountDet.setText(it.mask_adult.toString())
            tvChildAmountDet.setText(it.mask_child.toString())
            tvPhoneDet.setText(it.phone)
            tvAddressDet.setText(it.address)
        }

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













