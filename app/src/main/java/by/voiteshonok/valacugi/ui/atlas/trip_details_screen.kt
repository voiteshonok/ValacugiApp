package by.voiteshonok.valacugi.ui.atlas

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import by.voiteshonok.valacugi.domain.ItineraryDayWithSteps
import by.voiteshonok.valacugi.domain.ItineraryStep
import by.voiteshonok.valacugi.domain.Trip
import by.voiteshonok.valacugi.domain.TripItinerary
import by.voiteshonok.valacugi.ui.trips.TripDisplayFormatter

@Composable
fun TripDetailsScreen(
    modifier: Modifier = Modifier,
    viewModelFactory: ViewModelProvider.Factory
) {
    val viewModel: TripDetailsViewModel = viewModel(factory = viewModelFactory)
    val uiState: TripDetailsUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val itinerary: TripItinerary? = uiState.itinerary
    if (itinerary == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = if (uiState.isLoading) "LOADING..." else "TRIP NOT FOUND",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        item {
            TripDetailsHeader(trip = itinerary.trip)
        }
        item {
            TripDetailsGrid(trip = itinerary.trip)
        }
        if (uiState.canManageMembership) {
            item {
                TripMembershipButton(
                    label = uiState.membershipButtonLabel,
                    isEnabled = !uiState.isMembershipActionInProgress,
                    onClick = viewModel::onMembershipButtonClick
                )
            }
        }
        item {
            Text(
                text = "ITINERARY",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        items(items = itinerary.days, key = { dayWithSteps: ItineraryDayWithSteps -> dayWithSteps.day.id }) { dayWithSteps ->
            DayCard(day = dayWithSteps)
        }
        item {
            Spacer(modifier = Modifier.padding(bottom = 96.dp))
        }
    }
}

@Composable
private fun TripDetailsHeader(trip: Trip) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = trip.title,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "REF: ${trip.id}",
            modifier = Modifier.padding(top = 8.dp),
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun TripMembershipButton(
    label: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val testTag: String = if (label == "UNJOIN") {
        TripDetailsTestTags.UnjoinButton
    } else {
        TripDetailsTestTags.JoinButton
    }
    Button(
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        enabled = isEnabled,
        onClick = onClick
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
private fun TripDetailsGrid(trip: Trip) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TripDetailsGridCell(label = "DATES", value = TripDisplayFormatter.formatDates(trip = trip), modifier = Modifier.weight(1f))
        TripDetailsGridCell(label = "ROSTER", value = "${TripDisplayFormatter.formatPax(trip = trip)} PAX", modifier = Modifier.weight(1f))
        TripDetailsGridCell(label = "BUDGET", value = TripDisplayFormatter.formatBudget(trip = trip), modifier = Modifier.weight(1f))
        TripDetailsGridCell(label = "ASSIGNED", value = TripDisplayFormatter.formatAssigned(trip = trip), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TripDetailsGridCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.primary).padding(8.dp)) {
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
        Text(
            text = value,
            modifier = Modifier.padding(top = 8.dp),
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun DayCard(day: ItineraryDayWithSteps) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        Text(
            text = "DAY ${String.format("%02d", day.day.dayIndex)}",
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = day.day.title,
            modifier = Modifier.padding(top = 8.dp),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        if (day.steps.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                day.steps.forEach { step ->
                    StepRow(step = step)
                }
            }
        }
    }
}

@Composable
private fun StepRow(step: ItineraryStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = step.title,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = step.timeText ?: "[ ? ]",
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

