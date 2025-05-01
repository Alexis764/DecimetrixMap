package com.alexisarevalor.decimetrixmap.feature.map.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexisarevalor.decimetrixmap.R
import com.alexisarevalor.decimetrixmap.feature.map.data.Feature
import com.alexisarevalor.decimetrixmap.ui.theme.SearchbarBackground

@Composable
fun SearchBox(
    placeName: String,
    onValueChanged: (String) -> Unit,
    filteredPlaces: List<Feature>,
    setCurrentPlace: (Feature) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlacesDisplayed = placeName.isNotEmpty() && filteredPlaces.isNotEmpty()

    Column(
        modifier = modifier
    ) {
        MySearchBar(
            text = placeName,
            onValueChanged = { onValueChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            isPlacesDisplayed = isPlacesDisplayed
        )

        AnimatedVisibility(isPlacesDisplayed) {
            MyPlacesList(
                filteredPlaces = filteredPlaces,
                onItemClick = { setCurrentPlace(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(SearchbarBackground)
            )
        }
    }
}

@Composable
fun MySearchBar(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    isPlacesDisplayed: Boolean
) {
    OutlinedTextField(
        value = text,
        onValueChange = { onValueChanged(it) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
        placeholder = {
            Text("Search here")
        },
        leadingIcon = {
            Image(
                painter = painterResource(R.drawable.red_marker),
                contentDescription = null
            )
        },
        trailingIcon = {
            if (text.isNotBlank()) {
                IconButton(onClick = { onValueChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },
        shape = if (isPlacesDisplayed) {
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