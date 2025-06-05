package com.hom.pharmacy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hom.pharmacy.data.XXXPharmacyInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class PharmacyViewModel : ViewModel() {
    private var pharmacyInfo: XXXPharmacyInfo? = null   // 下載取得所有 gson資料
    private val TAG: String? = PharmacyViewModel::class.java.simpleName

    //    val pharmaciesDataUrl = "http://delexons.ddns.net:81/pharmacies/info.json"    // 有78筆地址資料空白
    val pharmaciesDataUrl = "http://delexons.ddns.net:81/pharmacies/info_132.json"
    var allCountiesName = mutableListOf<String>()   // 篩選 county
    var allTownName = mutableListOf<String>()       // 篩選 town 不能重複
    var getAllCountiesName = MutableLiveData<List<String>>()    // 監聽 county
    var getAllTownName = MutableLiveData<List<String>>()        // 監聽 town
    var getPharmacyInfo = MutableLiveData<XXXPharmacyInfo>()    // 監聽 gson

    fun vmPharmaciesData() {
        CoroutineScope(Dispatchers.IO).launch {
            val pharmaciesData = URL(pharmaciesDataUrl).readText()
            pharmacyInfo = Gson().fromJson(pharmaciesData, XXXPharmacyInfo::class.java)

            //  groupBy 群組篩選
            pharmacyInfo?.also {
                getPharmacyInfo.postValue(it)     // 傳遞 pharmacyInfo
                val countyData = it.features.groupBy { it.properties.county }
                countyData.forEach { county ->
                    allCountiesName.add(county.key)   // 篩選所有縣市
                }
                getAllCountiesName.postValue(allCountiesName)   // 傳遞 county
            }
        }
    }

    fun vmUpdateTown(currentCounty: String) {
        pharmacyInfo?.also {
            val townData = it.features.filter {
                it.properties.county == currentCounty //&& it.properties.town == "屏東市"
            }
            allTownName.clear()     // 清除資料
            townData.forEach {
                allTownName.add(it.properties.town)
            }
            allTownName = allTownName.distinct().toMutableList()    // 去除重複
            getAllTownName.postValue(allTownName)   // 傳遞 town
        }
    }

}




//   filter 篩選
//            pharmacyInfo?.also {
//                val data = it.features.filter {
//                    it.properties.county == "屏東縣" //&& it.properties.town == "屏東市"
//                }
//            data.forEach {
//                Log.d(TAG, "vmPharmaciesData: mask- filter- " +
//                        "${it.properties.town} , ${it.properties.name}")
//            }
//            val townData = county.value.groupBy { it.properties.town }
//            townData.forEach { town ->
//                town.key
//                town.value.forEach { pharmacy ->
//                    pharmacy.properties.name
//                    pharmacy.properties.mask_adult
//                    pharmacy.properties.mask_child
//                }
//            }
