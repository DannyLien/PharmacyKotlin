package com.hom.pharmacy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.hom.pharmacy.databinding.InfoWindowBinding

class MyInfoAdapter(mContext: Context) : GoogleMap.InfoWindowAdapter {
    val context = mContext

    override fun getInfoContents(marker: Marker): View? {
        val binding = InfoWindowBinding.inflate(
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        )
        render(marker, binding)
        return binding.root
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }

    private fun render(marker: Marker, binding: InfoWindowBinding) {
        val mask = marker.snippet.toString().split(",")
        binding.tvInfoName.setText(marker.title)
        binding.tvInfoAdultAmount.setText(mask.get(0))
        binding.tvInfoChildAmount.setText(mask.get(1))
    }


}
