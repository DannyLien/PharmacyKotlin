package com.hom.pharmacy

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hom.pharmacy.data.Feature
import com.hom.pharmacy.databinding.ItemViewBinding

class MainAdapter(val context: Context, val pharmacyList: List<Feature>) :
    RecyclerView.Adapter<MainAdapter.MyViewHolder>() {
    class MyViewHolder(var view: ItemViewBinding) : ViewHolder(view.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = ItemViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return pharmacyList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: Feature = pharmacyList.get(position)
        holder.view.tvName.setText(data.properties.name)
        holder.view.tvAdultAmount.setText(data.properties.mask_adult.toString())
        holder.view.tvChildAmount.setText(data.properties.mask_child.toString())
        holder.itemView.setOnClickListener {
            Intent(context, PharmacyDetail::class.java)
                .apply {
                    putExtra("DATA", data)
                }
                .also {
                    context.startActivity(it)
                }
        }
    }


}









