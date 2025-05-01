package com.alexisarevalor.decimetrixmap.feature.map

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
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

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()

        } else {
            mapViewModel.getPlaces()
        }
    }

    //Map States
    val mapViewportState = rememberMapViewportState()

    //Map content
    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
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
        }
    }
}


//val coroutineScope = rememberCoroutineScope()
//var cameraChanged by rememberSaveable { mutableStateOf(false) }
//val mapState = rememberMapState {
//    coroutineScope.launch {
//        cameraChangedEvents.collect { _ ->
//            cameraChanged = !cameraChanged
//        }
//    }
//}

// Set the map's min zoom
//val cameraBoundsOptions = CameraBoundsOptions
//    .Builder()
//    .minZoom(7.0)
//    .build()
//
//mapView.mapboxMap.setBounds(cameraBoundsOptions)

// Do something when the camera changes
//MapEffect(cameraChanged) { mapView ->
//    val cameraBounds = mapView
//        .mapboxMap
//        .coordinateBoundsForCamera(mapView.mapboxMap.cameraState.toCameraOptions())
//
//    mapViewModel.setCurrentPointsList(cameraBounds)
//}

// Draw the points on the map
//val currentPointList = mapViewModel.currentPointList
//currentPointList.forEach { place ->
//    CircleAnnotation(
//        point = Point.fromLngLat(
//            /* longitude = */ place.geometry.coordinates[0],
//            /* latitude = */ place.geometry.coordinates[1]
//        )
//    ) {
//        circleRadius = 8.0
//        circleColor = Color(0xffee4e8b)
//        circleStrokeWidth = 2.0
//        circleStrokeColor = Color(0xffffffff)
//        interactionsState.onClicked {
//            Toast.makeText(context, place.properties.name, Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//}