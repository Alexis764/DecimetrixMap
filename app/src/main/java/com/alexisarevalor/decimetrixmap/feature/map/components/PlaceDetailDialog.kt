package com.alexisarevalor.decimetrixmap.feature.map.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexisarevalor.decimetrixmap.R
import com.alexisarevalor.decimetrixmap.feature.map.data.Feature
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PlaceDetailDialog(currentPlaceSearched: Feature, onDismiss: () -> Unit) {
    val coordinatesFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    val populationFormatter = NumberFormat.getIntegerInstance(Locale.GERMANY)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider()
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Close",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        icon = {
            Image(
                painter = painterResource(R.drawable.red_marker),
                contentDescription = null
            )
        },
        title = {
            Text(
                text = currentPlaceSearched.properties.name
            )
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                PlaceFeatureText(
                    title = "Country: ",
                    description = currentPlaceSearched.properties.admName
                )
                PlaceFeatureText(
                    title = "Country code: ",
                    description = currentPlaceSearched.properties.admA3
                )
                PlaceFeatureText(
                    title = "Region: ",
                    description = currentPlaceSearched.properties.region
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                PlaceFeatureText(
                    title = "Latitude: ",
                    description = coordinatesFormatter.format(currentPlaceSearched.properties.latitude)
                )
                PlaceFeatureText(
                    title = "Longitude: ",
                    description = coordinatesFormatter.format(currentPlaceSearched.properties.longitude)
                )
                PlaceFeatureText(
                    title = "Maximum population: ",
                    description = populationFormatter.format(currentPlaceSearched.properties.popMax)
                )
                PlaceFeatureText(
                    title = "Minimum population: ",
                    description = populationFormatter.format(currentPlaceSearched.properties.popMin)
                )
            }
        }
    )
}

@Composable
fun PlaceFeatureText(title: String, description: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            text = description,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}