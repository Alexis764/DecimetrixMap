package com.alexisarevalor.decimetrixmap

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.alexisarevalor.decimetrixmap.core.navigation.NavigationWrapper
import com.alexisarevalor.decimetrixmap.main.LocationPermissionTextProvider
import com.alexisarevalor.decimetrixmap.main.MainViewModel
import com.alexisarevalor.decimetrixmap.main.PermissionDialog
import com.alexisarevalor.decimetrixmap.ui.theme.DecimetrixMapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //MainViewModel is injected using Hilt
    private val mainViewModel by viewModels<MainViewModel>()

    //List of permissions to request
    private val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DecimetrixMapTheme {
                //Requesting permissions
                val dialogQueue = mainViewModel.visiblePermissionDialogQueue

                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { perms ->
                        permissionsToRequest.forEach { permission ->
                            if (permission != Manifest.permission.ACCESS_FINE_LOCATION) {
                                if (perms.keys.contains(permission)) {
                                    mainViewModel.onPermissionResult(
                                        permission = permission,
                                        isGranted = perms[permission] == true
                                    )
                                }
                            }
                        }
                    }
                )

                //Launch request of permissions
                LaunchedEffect(Unit) { multiplePermissionResultLauncher.launch(permissionsToRequest) }

                //Permission dialog for each permission denied
                dialogQueue
                    .reversed()
                    .forEach { permission ->
                        PermissionDialog(
                            permissionTextProvider = when (permission) {
                                Manifest.permission.ACCESS_COARSE_LOCATION -> LocationPermissionTextProvider()
                                else -> return@forEach
                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
                            onDismiss = mainViewModel::dismissDialog,
                            onOkClick = {
                                mainViewModel.dismissDialog()
                                if (permission == Manifest.permission.ACCESS_COARSE_LOCATION) {
                                    multiplePermissionResultLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    )
                                } else {
                                    multiplePermissionResultLauncher.launch(arrayOf(permission))
                                }
                            },
                            onGoToAppSettingsClick = ::openAppSettings
                        )
                    }

                //Main content of the app
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationWrapper(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

//Extension function to open app settings
fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}