package com.alexisarevalor.decimetrixmap.feature.map.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alexisarevalor.decimetrixmap.R
import com.alexisarevalor.decimetrixmap.feature.map.data.Feature
import com.mapbox.maps.Style

@Composable
fun MapMenu(
    changeBaseMap: (String) -> Unit,
    centerUserPosition: () -> Unit,
    navigateToPointScreen: () -> Unit,
    currentPlaceSearched: Feature?,
    centerCurrentPlace: () -> Unit,
    modifier: Modifier = Modifier
) {
    var baseMapSelectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val baseMapOptions = listOf(
        BaseMap(Style.MAPBOX_STREETS, R.drawable.street),
        BaseMap(Style.STANDARD, R.drawable.standar),
        BaseMap(Style.SATELLITE, R.drawable.satellite)
    )

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        AnimatedVisibility(currentPlaceSearched != null) {
            SmallFloatingActionButton(
                onClick = { centerCurrentPlace() }
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        SmallFloatingActionButton(
            onClick = { centerUserPosition() }
        ) {
            Icon(
                painter = painterResource(R.drawable.center),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        SmallFloatingActionButton(
            onClick = { navigateToPointScreen() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        SingleChoiceSegmentedButtonRow {
            baseMapOptions.forEachIndexed { index, baseMap ->
                SegmentedButton(
                    selected = index == baseMapSelectedIndex,
                    onClick = {
                        baseMapSelectedIndex = index
                        changeBaseMap(baseMap.style)
                    },
                    shape = SegmentedButtonDefaults.itemShape(index, baseMapOptions.size),
                    label = {
                        Icon(
                            painter = painterResource(baseMap.icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }
        }
    }
}

data class BaseMap(
    val style: String,
    val icon: Int
)