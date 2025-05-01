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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexisarevalor.decimetrixmap.feature.map.components.SearchBox
import com.alexisarevalor.decimetrixmap.feature.map.data.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = hiltViewModel()
) {
    //Screen states
    val context = LocalContext.current

    val isConnected by mapViewModel.networkStatus.collectAsState()
    val isSearchingReady by mapViewModel.isSearchingReady.observeAsState(true)
    val placeName by mapViewModel.placeName.observeAsState("")
    val filteredPlaces = mapViewModel.filteredPlaces

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
}


@Composable
fun MyMap(
    mapViewportState: MapViewportState,
    currentPlaceSearched: Feature?,
    modifier: Modifier = Modifier
) {
    MapboxMap(
        modifier = modifier,
        mapViewportState = mapViewportState
    ) {
        // Map setup
        MapEffect(Unit) { mapView ->
            // Set the map's camera to the user's location
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()
        }

        if (currentPlaceSearched != null) {
            CircleAnnotation(
                point = Point.fromLngLat(
                    /* longitude = */ currentPlaceSearched.geometry.coordinates[0],
                    /* latitude = */ currentPlaceSearched.geometry.coordinates[1]
                )
            ) {
                circleRadius = 8.0
                circleColor = Color(0xffee4e8b)
                circleStrokeWidth = 2.0
                circleStrokeColor = Color(0xffffffff)
                interactionsState.onClicked {
                    true
                }
            }
        }

        MapEffect(currentPlaceSearched) {
            if (currentPlaceSearched != null) {
                mapViewportState.easeTo(
                    cameraOptions {
                        center(
                            Point.fromLngLat(
                                /* longitude = */ currentPlaceSearched.geometry.coordinates[0],
                                /* latitude = */ currentPlaceSearched.geometry.coordinates[1]
                            )
                        )
                        zoom(9.0)
                    }
                )
            }
        }
    }
}