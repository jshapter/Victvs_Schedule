package com.example.victvsschedule.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.victvsschedule.viewmodel.BaseViewModel

// drawer content for displaying filter options
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDrawerSheetContent(viewModel: BaseViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(
                text = "Location",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            // flow row of filterchips generated for each unique location by viewmodel function
            FlowRow(
                maxItemsInEachRow = 3
            ) {
                viewModel.GenerateLocationChips()
            }
        }
        HorizontalDivider(modifier = Modifier.padding(8.dp))
        Column {
            Text(
                text = "Candidate",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            // flow row of filterchips generated for each unique candidate by viewmodel function
            FlowRow(
                maxItemsInEachRow = 3
            ) {
                viewModel.GenerateCandidateChips()
            }
        }
        HorizontalDivider(modifier = Modifier.padding(8.dp))
        Column {
            Text(
                text = "Date",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            FlowRow(
                maxItemsInEachRow = 3
            ) {
                // flow row of filterchips generated for each unique date by viewmodel function
                viewModel.GenerateDateChips()
            }
        }
        HorizontalDivider(modifier = Modifier.padding(8.dp))
    }
}