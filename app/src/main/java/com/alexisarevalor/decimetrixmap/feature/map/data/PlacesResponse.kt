package com.alexisarevalor.decimetrixmap.feature.map.data

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    @SerializedName("features") val features: List<Feature>
)

data class Feature(
    @SerializedName("properties") val properties: Properties,
    @SerializedName("geometry") val geometry: Geometry
)

data class Properties(
    @SerializedName("scalerank") val scalerank: Int,
    @SerializedName("natscale") val natscale: Int,
    @SerializedName("labelrank") val labelrank: Int,
    @SerializedName("name") val name: String, // Place name
    @SerializedName("adm0cap") val isCapital: Int, //1 = Is capital, 0 = Is not capital
    @SerializedName("capalt") val isAlternativeCapital: Int, //1 = Is alternative capital, 0 = Is not alternative capital
    @SerializedName("adm0name") val admName: String, // Country name
    @SerializedName("adm0_a3") val admA3: String, // Country code
    @SerializedName("adm1name") val region: String, // Region
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("pop_max") val popMax: Int,
    @SerializedName("pop_min") val popMin: Int
)

data class Geometry(
    @SerializedName("coordinates") val coordinates: List<Double> // [0 = longitude,1 = latitude]
)