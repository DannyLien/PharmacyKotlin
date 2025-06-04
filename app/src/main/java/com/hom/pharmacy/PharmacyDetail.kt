package com.hom.pharmacy

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hom.pharmacy.data.Feature
import com.hom.pharmacy.databinding.ActivityPharmacyDetailBinding

class PharmacyDetail : AppCompatActivity() {
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
        pharmacyList?.also {
            Log.d(
                TAG, "onCreate: mask- intent- " +
                        "${it.properties.name} , ${it.properties.mask_adult} "
            )
        }

    }


}









