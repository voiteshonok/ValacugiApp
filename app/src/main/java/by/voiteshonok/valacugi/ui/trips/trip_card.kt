package by.voiteshonok.valacugi.ui.trips

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.voiteshonok.valacugi.domain.Trip

@Composable
fun TripCard(
    trip: Trip,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .padding(vertical = 24.dp, horizontal = 0.dp)
    ) {
        Text(
            text = trip.title,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TripMetricCell(
                label = "DATES",
                value = TripDisplayFormatter.formatDates(trip = trip),
                modifier = Modifier.weight(1f)
            )
            TripMetricCell(
                label = "PAX",
                value = TripDisplayFormatter.formatPax(trip = trip),
                modifier = Modifier.weight(1f)
            )
            TripMetricCell(
                label = "BUDGET",
                value = TripDisplayFormatter.formatBudget(trip = trip),
                modifier = Modifier.weight(1f)
            )
            TripMetricCell(
                label = "ASSIGNED",
                value = TripDisplayFormatter.formatAssigned(trip = trip),
                modifier = Modifier.weight(1f),
                isValueBold = true
            )
        }
    }
}

@Composable
private fun TripMetricCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isValueBold: Boolean = false
) {
    Column(
        modifier = modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Text(
            text = value,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isValueBold) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}
