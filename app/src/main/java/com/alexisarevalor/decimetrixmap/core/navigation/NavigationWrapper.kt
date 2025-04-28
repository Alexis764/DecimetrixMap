package com.alexisarevalor.decimetrixmap.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexisarevalor.decimetrixmap.feature.map.MapScreen

@Composable
fun NavigationWrapper(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Map,
        modifier = modifier
    ) {
        composable<Map> {
            MapScreen()
        }
    }
}