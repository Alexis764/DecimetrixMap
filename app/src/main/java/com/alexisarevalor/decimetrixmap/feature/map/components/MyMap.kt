package com.alexisarevalor.decimetrixmap.feature.map.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alexisarevalor.decimetrixmap.feature.map.data.Feature
import com.alexisarevalor.decimetrixmap.ui.theme.CircleAnnotationBackground
import com.alexisarevalor.decimetrixmap.ui.theme.CircleAnnotationBorderCapital
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
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
    modifier: Modifier = Modifier
) {
    MapboxMap(
        modifier = modifier,
        mapViewportState = mapViewportState,
        style = { MapStyle(style = mapStyle) }
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
    }
}