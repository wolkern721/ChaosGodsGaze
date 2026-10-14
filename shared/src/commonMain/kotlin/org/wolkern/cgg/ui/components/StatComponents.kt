package org.wolkern.cgg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wolkern.cgg.ui.theme.*

@Composable
fun MonitoringStatLabel(label: String, currentValue: Int, baseValue: Int) {
    val textColor = when {
        currentValue > baseValue -> BonusGreen
        currentValue < baseValue -> MalusRed
        else -> TextLight
    }
    Row {
        Text(text = "$label:", color = TextDark, fontSize = 13.sp)
        Text(text = currentValue.toString(), color = textColor, fontWeight = if (currentValue != baseValue) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp, modifier = Modifier.padding(start = 2.dp))
    }
}

@Composable
fun RowScope.StatCell(label: String, currentValue: Int, baseValue: Int, isDead: Boolean) {
    val textColor = when {
        isDead -> TextDark
        currentValue > baseValue -> BonusGreen
        currentValue < baseValue -> MalusRed
        else -> TextLight
    }
    val isModified = currentValue != baseValue && !isDead

    Column(
        modifier = Modifier
            .weight(1f)
            .background(CardBg, shape = RoundedCornerShape(6.dp))
            .border(1.dp, if (isModified) textColor.copy(alpha = 0.5f) else BorderColor, shape = RoundedCornerShape(6.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, color = TextDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = currentValue.toString(), color = textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}