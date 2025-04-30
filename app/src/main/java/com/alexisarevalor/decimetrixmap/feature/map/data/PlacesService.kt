package com.alexisarevalor.decimetrixmap.feature.map.data

import retrofit2.Response
import retrofit2.http.GET

interface PlacesService {

    @GET("ne_50m_populated_places_simple.geojson")
    suspend fun getPlaces(): Response<PlacesResponse>

}