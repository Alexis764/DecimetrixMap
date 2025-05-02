package com.alexisarevalor.decimetrixmap.core.storage

data class PointModel(
    val pointId: Int,
    val pointName: String,
    val pointLatitude: Double,
    val pointLongitude: Double,
    val pointAlert: Int
)
