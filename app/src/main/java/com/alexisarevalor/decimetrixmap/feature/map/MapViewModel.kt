package com.alexisarevalor.decimetrixmap.feature.map

import android.util.Log
import androidx.lifecycle.ViewModel
import com.alexisarevalor.decimetrixmap.feature.map.data.PlacesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placesService: PlacesService
) : ViewModel() {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val placesResponse = placesService.getPlaces()
            if (placesResponse.isSuccessful && placesResponse.body() != null) {
                Log.e("Places", "Places: ${placesResponse.body()!!.features}")
            }
        }
    }

}