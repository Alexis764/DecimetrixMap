package com.alexisarevalor.decimetrixmap.feature.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointModal(
    onDismiss: () -> Unit,
    onSave: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var pointName by rememberSaveable { mutableStateOf("") }
    var isAlertPoint by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Save new point",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            TextField(
                value = pointName,
                onValueChange = { if (it.length <= 20) pointName = it },
                label = { Text("Point name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                maxLines = 1
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Alert",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Special point with pulsations",
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.weight(1f))

                Switch(
                    checked = isAlertPoint,
                    onCheckedChange = { isAlertPoint = !isAlertPoint }
                )
            }

            Button(
                onClick = {
                    onSave(pointName, isAlertPoint)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = pointName.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}