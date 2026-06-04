package by.voiteshonok.valacugi.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.voiteshonok.valacugi.core.trip_creation.TripCreationDraft
import by.voiteshonok.valacugi.core.trip_creation.TripStepDraft
import by.voiteshonok.valacugi.ui.theme.AtlasError
import by.voiteshonok.valacugi.ui.theme.AtlasOnSurfaceVariant
import by.voiteshonok.valacugi.ui.theme.AtlasSafetyOrange
import by.voiteshonok.valacugi.ui.theme.AtlasSurfaceContainerHigh

private val StepActionTypes: List<String> = listOf(
    "Transit",
    "Deployment",
    "Logistics",
    "Standby"
)

@Composable
fun TripConstructorScreen(
    draft: TripCreationDraft,
    modifier: Modifier = Modifier,
    initialSteps: List<TripStepDraft> = emptyList(),
    onNavigateBack: () -> Unit,
    onFinished: () -> Unit,
    viewModelFactory: ViewModelProvider.Factory
) {
    val viewModel: TripConstructorViewModel = viewModel(factory = viewModelFactory)
    val constructorUiState: TripConstructorUiState by viewModel.uiState.collectAsStateWithLifecycle()
    var stepTitle: String by remember { mutableStateOf("") }
    var stepCoordinates: String by remember { mutableStateOf("") }
    var stepTimeOffset: String by remember { mutableStateOf("") }
    var stepActionType: String by remember { mutableStateOf(StepActionTypes.first()) }
    var selectedStepListIndex: Int? by remember { mutableStateOf(null) }
    val activeSteps: androidx.compose.runtime.snapshots.SnapshotStateList<TripStepDraft> = remember {
        mutableStateListOf()
    }
    var hasLoadedInitialSteps: Boolean by remember { mutableStateOf(false) }
    LaunchedEffect(initialSteps) {
        if (!hasLoadedInitialSteps && initialSteps.isNotEmpty()) {
            activeSteps.clear()
            activeSteps.addAll(initialSteps)
            hasLoadedInitialSteps = true
        }
    }
    val isEditingStep: Boolean = selectedStepListIndex != null
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TripCreationTopBar(
            onNavigateBack = onNavigateBack,
            trailingIcon = TripCreationTopBarTrailingIcon.Settings
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = draft.locationHeaderLabel,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "EXPEDITION ID: ${draft.expeditionId}",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = AtlasSafetyOrange
                )
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "INITIALIZE STEPS",
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            ConstructorInputField(
                fieldNumber = "01",
                label = "STEP TITLE",
                value = stepTitle,
                onValueChange = { newValue: String -> stepTitle = newValue },
                placeholder = "Basecamp Arrival"
            )
            ConstructorInputField(
                fieldNumber = "02",
                label = "COORDINATES",
                value = stepCoordinates,
                onValueChange = { newValue: String -> stepCoordinates = newValue },
                placeholder = "46.0207° N, 7.7491° E"
            )
            ConstructorInputField(
                fieldNumber = "03",
                label = "TIME/OFFSET",
                value = stepTimeOffset,
                onValueChange = { newValue: String -> stepTimeOffset = newValue },
                placeholder = "+00:00 HR"
            )
            ConstructorActionTypeField(
                selectedActionType = stepActionType,
                onActionTypeSelected = { actionType: String -> stepActionType = actionType }
            )
            if (isEditingStep) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                ) {
                    ConstructorActionButton(
                        text = "EDIT STEP",
                        modifier = Modifier.weight(1f),
                        isFilled = true,
                        onClick = {
                            val editIndex: Int = selectedStepListIndex ?: return@ConstructorActionButton
                            val trimmedTitle: String = stepTitle.trim()
                            if (trimmedTitle.isEmpty()) {
                                return@ConstructorActionButton
                            }
                            activeSteps[editIndex] = buildStepFromForm(
                                index = editIndex + 1,
                                title = trimmedTitle,
                                coordinates = stepCoordinates,
                                timeOffset = stepTimeOffset,
                                actionType = stepActionType
                            )
                            clearStepForm(
                                onTitleChange = { stepTitle = it },
                                onCoordinatesChange = { stepCoordinates = it },
                                onTimeOffsetChange = { stepTimeOffset = it },
                                onActionTypeChange = { stepActionType = it },
                                onSelectionChange = { selectedStepListIndex = it }
                            )
                            stepActionType = StepActionTypes.first()
                        }
                    )
                    ConstructorActionButton(
                        text = "REMOVE STEP",
                        modifier = Modifier
                            .weight(1f)
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                        isFilled = false,
                        onClick = {
                            val removeIndex: Int = selectedStepListIndex ?: return@ConstructorActionButton
                            activeSteps.removeAt(index = removeIndex)
                            reindexSteps(steps = activeSteps)
                            clearStepForm(
                                onTitleChange = { stepTitle = it },
                                onCoordinatesChange = { stepCoordinates = it },
                                onTimeOffsetChange = { stepTimeOffset = it },
                                onActionTypeChange = { stepActionType = it },
                                onSelectionChange = { selectedStepListIndex = it }
                            )
                            stepActionType = StepActionTypes.first()
                        }
                    )
                }
            } else {
                ConstructorActionButton(
                    text = "ADD STEP ->",
                    modifier = Modifier.fillMaxWidth(),
                    isFilled = true,
                    onClick = {
                        val trimmedTitle: String = stepTitle.trim()
                        if (trimmedTitle.isEmpty()) {
                            return@ConstructorActionButton
                        }
                        val nextIndex: Int = activeSteps.size + 1
                        activeSteps.add(
                            element = buildStepFromForm(
                                index = nextIndex,
                                title = trimmedTitle,
                                coordinates = stepCoordinates,
                                timeOffset = stepTimeOffset,
                                actionType = stepActionType
                            )
                        )
                        clearStepForm(
                            onTitleChange = { stepTitle = it },
                            onCoordinatesChange = { stepCoordinates = it },
                            onTimeOffsetChange = { stepTimeOffset = it },
                            onActionTypeChange = { stepActionType = it },
                            onSelectionChange = { selectedStepListIndex = it }
                        )
                        stepActionType = StepActionTypes.first()
                    }
                )
            }
            Text(
                text = "ACTIVE SEQUENCE",
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            ) {
                if (activeSteps.isEmpty()) {
                    item(key = "awaiting_input") {
                        AwaitingInputStepCard(stepIndex = 1)
                    }
                } else {
                    itemsIndexed(
                        items = activeSteps,
                        key = { index: Int, _: TripStepDraft -> "step_$index" }
                    ) { index: Int, step: TripStepDraft ->
                        ActiveSequenceStepCard(
                            step = step,
                            isSelected = selectedStepListIndex == index,
                            onClick = {
                                selectedStepListIndex = index
                                stepTitle = step.title
                                stepCoordinates = if (step.coordinates == "[ ? ]") "" else step.coordinates
                                stepTimeOffset = step.timeOffset
                                stepActionType = step.actionType
                            }
                        )
                    }
                }
            }
        }
        val finalizeLabel: String = if (constructorUiState.isFinalizing) {
            "FINALIZING..."
        } else {
            "FINALIZE ITINERARY"
        }
        if (!constructorUiState.errorMessage.isNullOrBlank()) {
            Text(
                text = constructorUiState.errorMessage.orEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = AtlasError
                )
            )
        }
        Text(
            text = finalizeLabel,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                .clickable(
                    enabled = !constructorUiState.isFinalizing,
                    onClick = {
                        viewModel.finalizeItinerary(
                            steps = activeSteps.toList(),
                            onSuccess = onFinished
                        )
                    }
                )
                .padding(vertical = 18.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

private fun buildStepFromForm(
    index: Int,
    title: String,
    coordinates: String,
    timeOffset: String,
    actionType: String
): TripStepDraft {
    return TripStepDraft(
        index = index,
        title = title.uppercase(),
        coordinates = coordinates.trim().ifEmpty { "[ ? ]" },
        timeOffset = timeOffset.trim().ifEmpty { "+00:00 HR" },
        actionType = actionType
    )
}

private fun reindexSteps(steps: MutableList<TripStepDraft>) {
    steps.forEachIndexed { stepListIndex: Int, step: TripStepDraft ->
        steps[stepListIndex] = step.copy(index = stepListIndex + 1)
    }
}

private fun clearStepForm(
    onTitleChange: (String) -> Unit,
    onCoordinatesChange: (String) -> Unit,
    onTimeOffsetChange: (String) -> Unit,
    onActionTypeChange: (String) -> Unit,
    onSelectionChange: (Int?) -> Unit
) {
    onTitleChange("")
    onCoordinatesChange("")
    onTimeOffsetChange("")
    onActionTypeChange(StepActionTypes.first())
    onSelectionChange(null)
}

@Composable
private fun ConstructorActionButton(
    text: String,
    isFilled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundModifier: Modifier = if (isFilled) {
        Modifier.background(MaterialTheme.colorScheme.primary)
    } else {
        Modifier.background(MaterialTheme.colorScheme.surface)
    }
    val textColor = if (isFilled) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }
    Text(
        text = text,
        modifier = modifier
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
            color = textColor
        )
    )
}

@Composable
private fun ConstructorInputField(
    fieldNumber: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "FIELD $fieldNumber: $label",
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = AtlasOnSurfaceVariant
                        )
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun ConstructorActionTypeField(
    selectedActionType: String,
    onActionTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "FIELD 04: ACTION TYPE",
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clickable {
                    val currentIndex: Int = StepActionTypes.indexOf(selectedActionType).coerceAtLeast(0)
                    val nextIndex: Int = (currentIndex + 1) % StepActionTypes.size
                    onActionTypeSelected(StepActionTypes[nextIndex])
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedActionType.ifEmpty { "Select Action" },
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = if (selectedActionType.isEmpty()) {
                        AtlasOnSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            )
            Text(
                text = "▼",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun ActiveSequenceStepCard(
    step: TripStepDraft,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTransit: Boolean = step.actionType.equals(other = "Transit", ignoreCase = true)
    val borderWidth: Int = if (isSelected) 2 else 1
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(width = borderWidth.dp, color = MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    } else {
                        AtlasSurfaceContainerHigh
                    }
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = step.index.toString().padStart(length = 2, padChar = '0'),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = step.title,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "${step.coordinates} | ${step.timeOffset}",
                    modifier = Modifier.padding(top = 6.dp),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = AtlasOnSurfaceVariant
                    )
                )
            }
            Text(
                text = step.actionType.uppercase(),
                modifier = Modifier
                    .then(
                        if (isTransit) {
                            Modifier.background(MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                    color = if (isTransit) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            )
        }
    }
}

@Composable
private fun AwaitingInputStepCard(
    stepIndex: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AtlasSurfaceContainerHigh)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = stepIndex.toString().padStart(length = 2, padChar = '0'),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        Text(
            text = "[ AWAITING INPUT ]",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = AtlasOnSurfaceVariant
            )
        )
    }
}
