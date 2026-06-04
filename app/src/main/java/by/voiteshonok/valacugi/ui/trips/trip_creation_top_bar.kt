package by.voiteshonok.valacugi.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TripCreationTopBarTrailingIcon {
    Notifications,
    Settings
}

@Composable
fun TripCreationTopBar(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    trailingIcon: TripCreationTopBarTrailingIcon = TripCreationTopBarTrailingIcon.Notifications
) {
    val trailingImageVector: ImageVector = when (trailingIcon) {
        TripCreationTopBarTrailingIcon.Notifications -> Icons.Filled.Notifications
        TripCreationTopBarTrailingIcon.Settings -> Icons.Filled.Settings
    }
    val trailingContentDescription: String = when (trailingIcon) {
        TripCreationTopBarTrailingIcon.Notifications -> "Notifications"
        TripCreationTopBarTrailingIcon.Settings -> "Settings"
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "VALACUGI",
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        IconButton(onClick = { }) {
            Icon(
                imageVector = trailingImageVector,
                contentDescription = trailingContentDescription,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
