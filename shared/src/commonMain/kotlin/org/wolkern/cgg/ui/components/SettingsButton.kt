package org.wolkern.cgg.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.wolkern.cgg.ui.theme.TzeentchGold

@Composable
fun SettingsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Paramètres",
            tint = TzeentchGold
        )
    }
}
