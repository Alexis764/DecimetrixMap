package com.alexisarevalor.decimetrixmap.feature.map

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexisarevalor.decimetrixmap.R
import com.alexisarevalor.decimetrixmap.feature.map.data.Feature
import com.alexisarevalor.decimetrixmap.ui.theme.SearchbarBackground
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
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp, vertical = 32.dp)
            ) {
                MySearchBar(
                    text = placeName,
                    onValueChanged = { mapViewModel.setPlaceName(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(placeName.isNotBlank()) {
                    MyPlacesList(
                        filteredPlaces = filteredPlaces,
                        onItemClick = { mapViewModel.setCurrentPlace(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                            .background(SearchbarBackground)
                    )
                }
            }
        }
    }
}


@Composable
fun MyPlacesList(
    filteredPlaces: List<Feature>,
    onItemClick: (Feature) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items = filteredPlaces) { index, place ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(place) }
            ) {
                Text(
                    text = place.properties.name,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (index != filteredPlaces.lastIndex) {
                    HorizontalDivider(color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MySearchBar(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text,
        onValueChange = { onValueChanged(it) },
        modifier = modifier,
        placeholder = {
            Text("Search here")
        },
        leadingIcon = {
            Image(
                painter = painterResource(R.drawable.red_marker),
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
        shape = if (text.isNotBlank()) {
            RoundedCornerShape(
                topStart = 28.dp,
                topEnd = 28.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )
        } else {
            ShapeDefaults.ExtraLarge
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SearchbarBackground,
            unfocusedContainerColor = SearchbarBackground,
            focusedPlaceholderColor = Color.LightGray,
            unfocusedPlaceholderColor = Color.LightGray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
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

                        zoom(10.0)
                    }
                )
            }
        }
    }
}