package com.alexisarevalor.decimetrixmap.feature.map.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.alexisarevalor.decimetrixmap.R
import com.alexisarevalor.decimetrixmap.core.storage.PointModel
import com.alexisarevalor.decimetrixmap.feature.map.data.Feature
import com.alexisarevalor.decimetrixmap.ui.theme.CircleAnnotationBackground
import com.alexisarevalor.decimetrixmap.ui.theme.CircleAnnotationBorderCapital
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

@Composable
fun MyMap(
    mapViewportState: MapViewportState,
    currentPlaceSearched: Feature?,
    currentPlaceClicked: () -> Unit,
    mapStyle: String,
    onMapLongClickListener: (Point) -> Unit,
    pointFromDatabase: PointModel?,
    modifier: Modifier = Modifier
) {
    val marker = rememberIconImage(
        key = R.drawable.red_marker,
        painter = painterResource(R.drawable.red_marker)
    )
    var userPoint by rememberSaveable { mutableStateOf<Point?>(null) }

    MapboxMap(
        modifier = modifier,
        mapViewportState = mapViewportState,
        style = { MapStyle(style = mapStyle) },
        onMapLongClickListener = { point ->
            userPoint = point
            onMapLongClickListener(point)
            true
        }
    ) {
        // Map setup
        MapEffect(Unit) { mapView ->
            // Set the map's camera to the user's location
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
                pulsingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()
        }

        // Functions to search for places on map
        if (currentPlaceSearched != null) {
            val isCapital = currentPlaceSearched.properties.isCapital == 1

            CircleAnnotation(
                point = Point.fromLngLat(
                    /* longitude = */ currentPlaceSearched.geometry.coordinates[0],
                    /* latitude = */ currentPlaceSearched.geometry.coordinates[1]
                )
            ) {
                circleRadius = 8.0
                circleColor = CircleAnnotationBackground
                circleStrokeWidth = 3.0
                circleStrokeColor = if (isCapital) CircleAnnotationBorderCapital else Color.White
                interactionsState.onClicked {
                    currentPlaceClicked()
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

        MapEffect(pointFromDatabase) {
            if (pointFromDatabase != null) {
                mapViewportState.easeTo(
                    cameraOptions {
                        center(
                            Point.fromLngLat(
                                /* longitude = */ pointFromDatabase.pointLongitude,
                                /* latitude = */ pointFromDatabase.pointLatitude
                            )
                        )
                        zoom(9.0)
                    }
                )
            }
        }

        // Create user marker
        userPoint?.let { PointAnnotation(point = it) { iconImage = marker } }
        pointFromDatabase?.let {
            PointAnnotation(
                point = Point.fromLngLat(
                    /* longitude = */ it.pointLongitude,
                    /* latitude = */ it.pointLatitude
                )
            )
            {
                iconImage = marker
                textField = it.pointName
                textOffset = listOf(0.0, 1.2)
            }
        }
    }
}