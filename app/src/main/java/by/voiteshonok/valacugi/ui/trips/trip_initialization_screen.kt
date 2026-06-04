package by.voiteshonok.valacugi.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.voiteshonok.valacugi.core.trip_creation.TripCreationDraft
import by.voiteshonok.valacugi.core.trip_creation.generateExpeditionId
import by.voiteshonok.valacugi.ui.theme.AtlasOnSurfaceVariant

@Composable
fun TripInitializationScreen(
    modifier: Modifier = Modifier,
    initialDraft: TripCreationDraft? = null,
    onNavigateBack: () -> Unit,
    onContinueTrip: (TripCreationDraft) -> Unit
) {
    val isEditMode: Boolean = initialDraft?.isEditMode == true
    var location: String by remember(initialDraft) { mutableStateOf(initialDraft?.location.orEmpty()) }
    var dateRangeText: String by remember(initialDraft) { mutableStateOf(initialDraft?.dateRangeText.orEmpty()) }
    var pax: String by remember(initialDraft) { mutableStateOf(initialDraft?.pax.orEmpty()) }
    var budget: String by remember(initialDraft) { mutableStateOf(initialDraft?.budget.orEmpty()) }
    var roster: String by remember(initialDraft) { mutableStateOf(initialDraft?.roster.orEmpty()) }
    val screenTitle: String = if (isEditMode) "EDIT EXPEDITION" else "NEW EXPEDITION"
    val expeditionIdLabel: String = if (isEditMode) {
        "[ ID: ${initialDraft?.expeditionId.orEmpty()} ]"
    } else {
        "[ ID: PENDING_GEN ]"
    }
    val continueButtonLabel: String = if (isEditMode) "UPDATE TRIP ->" else "INITIALIZE TRIP ->"
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TripCreationTopBar(
            onNavigateBack = onNavigateBack,
            trailingIcon = TripCreationTopBarTrailingIcon.Notifications
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Text(
                    text = screenTitle,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = expeditionIdLabel,
                    modifier = Modifier.padding(top = 8.dp),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = AtlasOnSurfaceVariant
                    )
                )
            }
            TripCreationFormField(
                fieldNumber = "01",
                label = "LOCATION",
                value = location,
                onValueChange = { newValue: String -> location = newValue },
                placeholder = "ENTER COORDINATES OR CITY"
            )
            TripCreationFormField(
                fieldNumber = "02",
                label = "DATES",
                value = dateRangeText,
                onValueChange = { newValue: String -> dateRangeText = newValue },
                placeholder = "[ ? ] - [ ? ]"
            )
            TripCreationFormField(
                fieldNumber = "03",
                label = "PAX",
                value = pax,
                onValueChange = { newValue: String -> pax = newValue },
                placeholder = "00"
            )
            TripCreationFormField(
                fieldNumber = "04",
                label = "BUDGET",
                value = budget,
                onValueChange = { newValue: String -> budget = newValue },
                placeholder = "CURRENCY VALUE"
            )
            TripCreationFormField(
                fieldNumber = "05",
                label = "ROSTER",
                value = roster,
                onValueChange = { newValue: String -> roster = newValue },
                placeholder = "TEAM ASSIGNMENT [ ? ]"
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    val expeditionId: String = if (isEditMode) {
                        initialDraft?.expeditionId.orEmpty()
                    } else {
                        generateExpeditionId()
                    }
                    val draft: TripCreationDraft = TripCreationDraft(
                        expeditionId = expeditionId,
                        location = location,
                        dateRangeText = dateRangeText,
                        pax = pax,
                        budget = budget,
                        roster = roster,
                        editingTripId = initialDraft?.editingTripId
                    )
                    onContinueTrip(draft)
                }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = continueButtonLabel,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
