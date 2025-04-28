package com.alexisarevalor.decimetrixmap.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(text = "Permission required") },
        text = { Text(text = permissionTextProvider.getDescription(isPermanentlyDeclined)) },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider()
                TextButton(
                    onClick = if (isPermanentlyDeclined) onGoToAppSettingsClick else onOkClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isPermanentlyDeclined) "Grant permission" else "OK",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class LocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Location permission is required to use this feature. Please grant it in the app settings."
        } else {
            "Location permission is required to use this feature. Please grant it."
        }
    }
}