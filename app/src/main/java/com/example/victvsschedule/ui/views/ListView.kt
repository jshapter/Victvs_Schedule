package com.example.victvsschedule.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.victvsschedule.ExamEvent
import com.example.victvsschedule.data.remote.dto.ExamResponse
import com.example.victvsschedule.ui.UiState
import com.example.victvsschedule.ui.components.ExamCard
import kotlinx.coroutines.flow.MutableStateFlow

// composable to display list of exams
@Composable
fun ListView(
    uiState: MutableStateFlow<UiState>,
    onEvent: (ExamEvent) -> Unit
) {
    // set required values
    val collectedUiState: State<UiState> = uiState.collectAsState()
    val exams = collectedUiState.value.allExams
    val filteredExams = collectedUiState.value.filteredExams
    val filterApplied = collectedUiState.value.filterApplied
    var showMap by remember { mutableStateOf(false) }
    val visibleExams: List<ExamResponse> = filteredExams.ifEmpty {
        exams
    }
    val tabState by remember { mutableIntStateOf(uiState.value.tabIndex) }

    // show map if selected from card onclick function
    if (showMap) {
        MapView(uiState = uiState, onEvent = onEvent)
        // display 'no result' message if filter returns no matching exams
    } else if ((filterApplied) && (filteredExams.isEmpty())) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "No filter results",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        // display list of visible exams based on filter options (all exams if no filter applied)
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            // generate card for each exam to be displayed. include arguments for selected exam onclick function to display selected exam map determined by its id
            items(visibleExams) { exam ->
                ExamCard(
                    exam,
                    modifier = Modifier.clickable {
                        onEvent(ExamEvent.SelectForMap(id = exam.id))
                        onEvent(ExamEvent.SelectTab(tabState))
                        showMap = true
                    }
                )
            }
        }
    }
}