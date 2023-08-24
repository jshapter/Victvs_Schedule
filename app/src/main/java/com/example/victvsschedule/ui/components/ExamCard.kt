package com.example.victvsschedule.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.victvsschedule.data.remote.Exam

// exam card composable template
@Composable
fun ExamCard(
    exam: Exam,
    modifier: Modifier
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                text = exam.examDescription,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // display formatted date & time
            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = exam.formattedExamDate
                )
                Text(
                    text = exam.formattedExamTime,
                    fontSize = 14.sp
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                // modifier from arguments will send id to event handler to update ui for selected exam
                Row(
                    modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "location")
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = exam.locationName
                    )
                }
            }
        }
    }
}