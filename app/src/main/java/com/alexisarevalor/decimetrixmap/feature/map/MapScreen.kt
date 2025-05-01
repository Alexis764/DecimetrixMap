package com.alexisarevalor.decimetrixmap.feature.map

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexisarevalor.decimetrixmap.feature.map.components.MyMap
import com.alexisarevalor.decimetrixmap.feature.map.components.PlaceDetailDialog
import com.alexisarevalor.decimetrixmap.feature.map.components.SearchBox
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = hiltViewModel()
) {
    //Screen states
    val context = LocalContext.current

    val isConnected by mapViewModel.networkStatus.collectAsState()
    val isSearchingReady by mapViewModel.isSearchingReady.observeAsState(false)

    val placeName by mapViewModel.placeName.observeAsState("")
    val filteredPlaces = mapViewModel.filteredPlaces

    val isDetailDialogVisible by mapViewModel.isDetailDialogVisible.observeAsState(false)

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()

        } else {
            mapViewModel.getPlaces()
            mapViewModel.showSearchBar()
        }
    }

    //Map States
    val mapViewportState = rememberMapViewportState()
    val currentPlaceSearched by mapViewModel.currentPlaceSearched.observeAsState(null)

    //Screen content
    Box(modifier = Modifier.fillMaxSize()) {
        MyMap(
            mapViewportState = mapViewportState,
            currentPlaceSearched = currentPlaceSearched,
            currentPlaceClicked = { mapViewModel.showDetailDialog() },
            modifier = Modifier.fillMaxSize()
        )

        if (isSearchingReady) {
            SearchBox(
                placeName = placeName,
                onValueChanged = { mapViewModel.setPlaceName(it) },
                filteredPlaces = filteredPlaces,
                setCurrentPlace = { mapViewModel.setCurrentPlace(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 52.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }

    if (isDetailDialogVisible && currentPlaceSearched != null) {
        PlaceDetailDialog(
            currentPlaceSearched = currentPlaceSearched!!,
            onDismiss = { mapViewModel.hideDetailDialog() }
        )
    }
}