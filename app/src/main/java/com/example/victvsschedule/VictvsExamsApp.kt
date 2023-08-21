package com.example.victvsschedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.victvsschedule.ui.UiState
import com.example.victvsschedule.viewmodel.BaseViewModel

@Composable
fun VictvsExamsApp(
    viewModel: BaseViewModel
) {
    // collect current ui state
    val collectedUiState: State<UiState> = viewModel.uiState.collectAsState()

    // display loading screen if waiting for API response
    if (collectedUiState.value.allExams.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LinearProgressIndicator()
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Fetching exam data")
                }
            }
        }
        // redirect to main app view when exam list is populated
    } else {
        ScreenScaffold(viewModel = viewModel)
    }
}