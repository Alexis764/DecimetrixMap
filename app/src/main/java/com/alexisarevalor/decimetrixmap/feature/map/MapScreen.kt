package com.alexisarevalor.decimetrixmap.feature.map

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.toCameraOptions
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = hiltViewModel()
) {
    //Screen states
    val currentPointList = mapViewModel.currentPointList
    val context = LocalContext.current

    //Map States
    val mapViewportState = rememberMapViewportState()

    val coroutineScope = rememberCoroutineScope()
    var cameraChanged by rememberSaveable { mutableStateOf(false) }
    val mapState = rememberMapState {
        coroutineScope.launch {
            cameraChangedEvents.collect { _ ->
                cameraChanged = !cameraChanged
            }
        }
    }

    //Map content
    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            mapState = mapState
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

                // Set the map's min zoom
                val cameraBoundsOptions = CameraBoundsOptions
                    .Builder()
                    .minZoom(7.0)
                    .build()

                mapView.mapboxMap.setBounds(cameraBoundsOptions)

                // Get the places from the API
                mapViewModel.getPlaces()
            }

            // Do something when the camera changes
            MapEffect(cameraChanged) { mapView ->
                val cameraBounds = mapView
                    .mapboxMap
                    .coordinateBoundsForCamera(mapView.mapboxMap.cameraState.toCameraOptions())

                mapViewModel.setCurrentPointsList(cameraBounds)
            }

            // Draw the points on the map
            currentPointList.forEach { place ->
                CircleAnnotation(
                    point = Point.fromLngLat(
                        /* longitude = */ place.geometry.coordinates[0],
                        /* latitude = */ place.geometry.coordinates[1]
                    )
                ) {
                    circleRadius = 8.0
                    circleColor = Color(0xffee4e8b)
                    circleStrokeWidth = 2.0
                    circleStrokeColor = Color(0xffffffff)
                    interactionsState.onClicked {
                        Toast.makeText(context, place.properties.name, Toast.LENGTH_SHORT).show()
                        true
                    }
                }
            }
        }
    }
}