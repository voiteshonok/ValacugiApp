package by.voiteshonok.valacugi.ui.trips

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import by.voiteshonok.valacugi.domain.Trip

@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    onOpenAtlas: (String) -> Unit,
    onOpenTripConstructor: () -> Unit,
    viewModelFactory: ViewModelProvider.Factory
) {
    val viewModel: TripsViewModel = viewModel(factory = viewModelFactory)
    val uiState: TripsUiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.title,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "ADD",
                modifier = Modifier
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onOpenTripConstructor)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            items(items = uiState.trips, key = { trip: Trip -> trip.id }) { trip: Trip ->
                TripCard(
                    trip = trip,
                    onClick = { onOpenAtlas(trip.id) }
                )
            }
        }
    }
}
