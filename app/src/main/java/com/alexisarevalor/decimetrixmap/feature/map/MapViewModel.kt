package com.alexisarevalor.decimetrixmap.feature.map

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisarevalor.decimetrixmap.core.network.NetworkConnectivityObserver
import com.alexisarevalor.decimetrixmap.feature.map.data.Feature
import com.alexisarevalor.decimetrixmap.feature.map.data.PlacesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placesService: PlacesService,
    connectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    //States
    private val _isSearchingReady = MutableLiveData<Boolean>()
    val isSearchingReady: LiveData<Boolean> = _isSearchingReady

    fun showSearchBar() {
        _isSearchingReady.value = true
    }

    private val _isDetailDialogVisible = MutableLiveData<Boolean>()
    val isDetailDialogVisible: LiveData<Boolean> = _isDetailDialogVisible

    fun showDetailDialog() {
        _isDetailDialogVisible.value = true
    }

    fun hideDetailDialog() {
        _isDetailDialogVisible.value = false
    }

    private val _isModalPointVisible = MutableLiveData<Boolean>()
    val isModalPointVisible: LiveData<Boolean> = _isModalPointVisible

    fun showModalPoint() {
        _isModalPointVisible.value = true
    }

    fun hideModalPoint() {
        _isModalPointVisible.value = false
    }


    //Init places list
    private val placesList = mutableListOf<Feature>()

    val networkStatus = connectivityObserver.networkStatus
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun getPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            val placesResponse = placesService.getPlaces()
            if (placesResponse.isSuccessful && placesResponse.body() != null) {
                placesList.clear()
                placesList.addAll(placesResponse.body()!!.features)
            }
        }
    }


    //Search for places in the list
    private val _placeName = MutableLiveData<String>()
    val placeName: LiveData<String> = _placeName

    private val _filteredPlaces = mutableStateListOf<Feature>()
    val filteredPlaces: List<Feature> = _filteredPlaces

    fun setPlaceName(name: String) {
        _placeName.value = name
        if (name.isNotBlank()) filterPlacesByName(name)
    }

    private fun filterPlacesByName(name: String) {
        _filteredPlaces.clear()
        _filteredPlaces.addAll(
            placesList.filter { place ->
                place
                    .properties
                    .name
                    .lowercase()
                    .contains(name.trim().lowercase())
            }
        )
    }


    //Search place on map
    private val _currentPlaceSearched = MutableLiveData<Feature?>(null)
    val currentPlaceSearched: LiveData<Feature?> = _currentPlaceSearched

    fun setCurrentPlace(place: Feature) {
        _currentPlaceSearched.value = place
        _filteredPlaces.clear()
    }

}