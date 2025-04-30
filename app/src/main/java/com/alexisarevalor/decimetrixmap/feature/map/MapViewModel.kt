package com.alexisarevalor.decimetrixmap.feature.map

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisarevalor.decimetrixmap.feature.map.data.Feature
import com.alexisarevalor.decimetrixmap.feature.map.data.PlacesService
import com.mapbox.geojson.Point
import com.mapbox.maps.CoordinateBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placesService: PlacesService
) : ViewModel() {

    private val placesList = mutableListOf<Feature>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val placesResponse = placesService.getPlaces()
            if (placesResponse.isSuccessful && placesResponse.body() != null) {
                placesList.addAll(placesResponse.body()!!.features)
            }
        }
    }


    private val _currentPointsList = mutableStateListOf<Feature>()
    val currentPointList: List<Feature> = _currentPointsList

    fun setCurrentPointsList(cameraBounds: CoordinateBounds) {
        val points = placesList.filter { place ->
            val longitude = place.geometry.coordinates[0]
            val latitude = place.geometry.coordinates[1]
            val point = Point.fromLngLat(longitude, latitude)
            cameraBounds.contains(point, true)
        }

        _currentPointsList.clear()
        _currentPointsList.addAll(points)
    }

}