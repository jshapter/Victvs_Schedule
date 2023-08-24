package com.example.victvsschedule.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.victvsschedule.ExamEvent
import com.example.victvsschedule.data.remote.Exam
import com.example.victvsschedule.ui.UiState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.flow.MutableStateFlow


// Map view to be shown in tab[1]
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapView(
    uiState: MutableStateFlow<UiState>,
    onEvent: (ExamEvent) -> Unit
) {
    // get data from UI state
    val collectedUiState: State<UiState> = uiState.collectAsState()
    val exams = collectedUiState.value.allExams
    val filteredExams = collectedUiState.value.filteredExams
    val visibleExams: List<Exam> = filteredExams.ifEmpty {
        exams
    }
    // use first exam in list if choosing map view from tab row
    val examSelected = collectedUiState.value.examSelected
    if (!examSelected) {
        onEvent(ExamEvent.SelectForMap(visibleExams[0].id))
    }
    val selectedTitle = collectedUiState.value.selectedTitle
    val selectedDescription = collectedUiState.value.selectedDescription
    val selectedLocation = collectedUiState.value.selectedLocation
    val selectedDate = collectedUiState.value.selectedDate
    val selectedTime = collectedUiState.value.selectedTime
    val selectedName = collectedUiState.value.selectedName
    val selectedEmail = collectedUiState.value.selectedEmail
    val selectedLatLng = collectedUiState.value.selectedLatLng

    // set map camera and initial marker state
    val initMarkerState = selectedLatLng?.let { rememberMarkerState(position = it) }
    val cameraPositionState = selectedLatLng?.let { CameraPosition.fromLatLngZoom(it, 12.5f) }
        ?.let { CameraPositionState(it) }

    // boolean to show initial marker info window on map load
    var initWindowContent by remember { mutableStateOf(true) }

    // bottom sheet state
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (cameraPositionState != null) {
        // load map with camera position set to selected exam location
        GoogleMap(
            cameraPositionState = cameraPositionState,
            contentDescription = "map of victvs exams"
        ) {
            // show info window for initial exam on load
            if (initWindowContent) {
                initMarkerState?.showInfoWindow()
            }

            // generate marker for initial exam (known issue of UI lagging if selected for too long, possible solution to offload to coroutine or boolean to stop this from looping?)
            if (initMarkerState != null) {
                MarkerInfoWindowContent(
                    state = initMarkerState,
                    content = {
                        if (selectedTitle != null) {
                            Box(modifier = Modifier.fillMaxWidth(0.3f)) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .padding(
                                                start = 10.dp,
                                                end = 10.dp,
                                                top = 12.dp,
                                                bottom = 0.dp
                                            )
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = selectedTitle,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .padding(bottom = 4.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "view info",
                                            color = Color.Black,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    },
                    // show info window on click
                    onClick = {
                        initWindowContent = true
                        true
                    },
                    // open bottomsheet on info window click
                    onInfoWindowClick = {
                        showBottomSheet = true
                    }
                )
            }

            // generate marker for each exam in filtered list or all exams if no filter applied
            visibleExams.forEach { exam: Exam ->
                // set position and marker state
                val latLng = LatLng(
                    exam.latitude.toDouble(),
                    exam.longitude.toDouble()
                )
                val markerState = rememberMarkerState(
                    position = latLng
                )
                val examId = exam.id
                MarkerInfoWindowContent(
                    state = markerState,
                    content = {
                        Box(modifier = Modifier.fillMaxWidth(0.3f)) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .padding(
                                            start = 10.dp,
                                            end = 10.dp,
                                            top = 12.dp,
                                            bottom = 0.dp
                                        )
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = exam.title,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "view info",
                                        color = Color.Black,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    },
                    // hide initial info window on click and show this info window
                    onClick = {
                        initWindowContent = false
                        markerState.showInfoWindow()
                        true
                    },
                    // load current exam data to ui state and open bottom sheet to display
                    onInfoWindowClick = {
                        onEvent(ExamEvent.SelectForMap(id = examId))
                        showBottomSheet = true
                    }
                )
            }
            // show bottom sheet with appropriate exam data if triggered boolean by selecting info window
            if (showBottomSheet) {
                ModalBottomSheet(
                    modifier = Modifier
                        .fillMaxHeight(0.5f),
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (selectedDescription != null) {
                                    Text(
                                        text = selectedDescription,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (selectedLocation != null) {
                                    Text(text = selectedLocation)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (selectedDate != null) {
                                    Text(
                                        text = selectedDate,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 12.sp,
                                        lineHeight = 8.sp
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (selectedTime != null) {
                                    Text(
                                        text = selectedTime,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 12.sp,
                                        lineHeight = 8.sp
                                    )
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(28.dp))
                            Row {
                                Icon(
                                    Icons.Default.Person, contentDescription = "candidate"
                                )
                                if (selectedName != null) {
                                    Text(
                                        text = selectedName,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row {
                                Icon(
                                    Icons.Default.Email, contentDescription = "email"
                                )
                                if (selectedEmail != null) {
                                    Text(
                                        text = selectedEmail,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}