package com.example.victvsschedule.viewmodel

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.victvsschedule.ExamEvent
import com.example.victvsschedule.data.remote.ExamsService
import com.example.victvsschedule.data.remote.dto.ExamResponse
import com.example.victvsschedule.ui.UiState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


// viewModel class for handling data, events and updating UI
class BaseViewModel: ViewModel() {

    // value of UI state
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState

    // contact API for exam data on initialization and update UI state
    init {
        getExams(ExamsService.create())
    }
    private fun getExams(service: ExamsService) {

        viewModelScope.launch {
            // use http service to contact api and sort results by date
            val apiResults = service.getExams().sortedBy { it.examdate }

            // get unique values of locations, candidates and dates for UI state
            val locations = emptyList<String>().toMutableList()
            val candidates = emptyList<String>().toMutableList()
            val dates = emptyList<String>().toMutableList()
            for (x in apiResults) {
                if (x.locationname !in locations) {
                    locations.add(x.locationname)
                }
            }
            for (x in apiResults) {
                if (x.candidatename !in candidates) {
                    candidates.add(x.candidatename)
                }
            }
            for (x in apiResults) {
                if (x.examdate !in dates) {
                    dates.add(x.examdate)
                }
            }

            // uncomment to demonstrate loading screen for 3 seconds
//            delay(3000)
            ////////////////////////////////////////////////////////

            // update UI state
            _uiState.update { it.copy(
                allExams = apiResults,
                locations = locations,
                candidates = candidates,
                dates = dates,
            ) }
        }
    }

    // event handler
    fun onEvent(event: ExamEvent) {
        when (event) {

            // update UI when exam is selected for map
            is ExamEvent.SelectForMap -> {
                val id = event.id
                for (x in _uiState.value.allExams) {
                    if (id == x.id) {
                        val newDateTime = formatDateTime(x.examdate)
                        _uiState.update { it.copy(
                            examSelected = true,
                            selectedId = id,
                            selectedTitle = x.title,
                            selectedDescription = x.examdescription,
                            selectedLocation = x.locationname,
                            selectedDate = newDateTime[0],
                            selectedTime = newDateTime[1],
                            selectedName = x.candidatename,
                            selectedEmail = x.candidateemail,
                            selectedLatLng = LatLng(
                                x.latitude.toDouble(),
                                x.longitude.toDouble()
                            )
                        ) }
                    }
                }
            }

            // update UI when new tab is selected (method required for changing the tab through events other than selecting the tab, i.e. choosing exam from list to view on map)
            is ExamEvent.SelectTab -> {
                val index = event.index

                if (index == 0) {
                    _uiState.update { it.copy(
                        tabIndex = 1,
                        onListView = false
                    ) }
                } else {
                    _uiState.update { it.copy(
                        tabIndex = 0,
                        onListView = true,
                        examSelected = false
                    ) }
                }
            }

            // update UI when filter options are applied/changed
            is ExamEvent.Filter -> {
                viewModelScope.launch {
                    // define necessary values
                    val option = event.option
                    val filterType = event.filterType
                    val filterApplied: Boolean
                    val exams = uiState.value.allExams
                    val filteredExams = emptyList<ExamResponse>().toMutableList()
                    val filteredByLocation = uiState.value.filteredByLocation.toMutableList()
                    var locationFilter = false
                    val filteredByCandidate = uiState.value.filteredByCandidate.toMutableList()
                    var candidateFilter = false
                    val filteredByDate = uiState.value.filteredByDate.toMutableList()
                    var dateFilter = false

                    // determine filter type, update appropriate list accordingly and update UI state
                    when (filterType) {
                        "location" -> {
                            for (x in exams) {
                                if (option == x.locationname) {
                                    if (x in filteredByLocation) {
                                        filteredByLocation.remove(x)
                                    } else {
                                        filteredByLocation.add(x)
                                    }
                                }
                            }
                            _uiState.update { it.copy(
                                filteredByLocation = filteredByLocation
                            ) }
                        }

                        "candidate" -> {
                            for (x in exams) {
                                if (option == x.candidatename) {
                                    if (x in filteredByCandidate) {
                                        filteredByCandidate.remove(x)
                                    } else {
                                        filteredByCandidate.add(x)
                                    }
                                }
                            }
                            _uiState.update { it.copy(
                                filteredByCandidate = filteredByCandidate
                            ) }
                        }

                        "date" -> {
                            for (x in exams) {
                                if (option == x.examdate) {
                                    if (x in filteredByDate) {
                                        filteredByDate.remove(x)
                                    } else {
                                        filteredByDate.add(x)
                                    }
                                }
                            }
                            _uiState.update { it.copy(
                                filteredByDate = filteredByDate
                            ) }
                        }
                    }

                    // determine which filters are applied
                    if (filteredByLocation.isNotEmpty()) { locationFilter = true }
                    if (filteredByCandidate.isNotEmpty()) { candidateFilter = true }
                    if (filteredByDate.isNotEmpty()) { dateFilter = true }

                    // main filtering algorithm to cover each combination of filters and update UI state
                    if (locationFilter && candidateFilter && dateFilter) {
                        Log.d(ContentValues.TAG, "Filtered by location, candidate & date!")
                        filterApplied = true
                        for (x in exams) {
                            if ((x in filteredByLocation) && (x in filteredByCandidate) && (x in filteredByDate)) {
                                filteredExams.add(x)
                                _uiState.update { it.copy(
                                    filteredExams = filteredExams
                                ) }
                            } else {
                                filteredExams.remove(x)
                                _uiState.update { it.copy(
                                    filteredExams = filteredExams
                                ) }
                            }
                        }
                    } else if (locationFilter && candidateFilter) {
                        Log.d(ContentValues.TAG, "Filtered by location & candidate!")
                        filterApplied = true
                        for (x in exams) {
                            if ((x in filteredByLocation) && (x in filteredByCandidate)) {
                                filteredExams.add(x)
                                _uiState.update { it.copy(
                                    filteredExams = filteredExams
                                ) }
                            } else {
                                filteredExams.remove(x)
                                _uiState.update { it.copy(
                                    filteredExams = filteredExams
                                ) }
                            }
                        }
                    } else if (locationFilter && dateFilter) {
                        Log.d(ContentValues.TAG, "Filtered by location & date!")
                        filterApplied = true
                        for (x in exams) {
                            if ((x in filteredByLocation) && (x in filteredByDate)) {
                                filteredExams.add(x)
                                _uiState.update { it.copy(
                                    filteredExams = filteredExams
                                ) }
                            } else {
                                filteredExams.remove(x)
                                _uiState.update { it.copy(
                                    filteredExams = filteredExams
                                ) }
                            }
                        }
                    } else if (candidateFilter && dateFilter) {
                        Log.d(ContentValues.TAG, "Filtered by candidate & date")
                        filterApplied = true
                        for (x in exams) {
                            if ((x in filteredByCandidate) && (x in filteredByDate)) {
                                filteredExams.add(x)
                                _uiState.update { it.copy(
                                    filteredExams = filteredExams
                                ) }
                            } else {
                                filteredExams.remove(x)
                                _uiState.update { it.copy(
                                    filteredExams = filteredExams
                                ) }
                            }
                        }
                    } else if (locationFilter) {
                        Log.d(ContentValues.TAG, "Filtered by location!")
                        filterApplied = true
                        _uiState.update { it.copy(
                            filteredExams = filteredByLocation
                        ) }
                    } else if (candidateFilter) {
                        Log.d(ContentValues.TAG, "Filtered by Candidate!")
                        filterApplied = true
                        _uiState.update { it.copy(
                            filteredExams = filteredByCandidate
                        ) }
                    } else if (dateFilter) {
                        Log.d(ContentValues.TAG, "Filtered by Date!")
                        filterApplied = true
                        _uiState.update { it.copy(
                            filteredExams = filteredByDate
                        ) }
                    } else {
                        Log.d(ContentValues.TAG, "No filter applied!")
                        filterApplied = false
                        _uiState.update { it.copy(
                            filteredExams = emptyList()
                        ) }
                    }

                    // update 'filter applied' boolean
                    _uiState.update { it.copy(
                        filterApplied = filterApplied
                    ) }

                    // update 'exam selected' boolean
                    _uiState.update { it.copy(
                        examSelected = false
                    ) }
                }
            }
        }
    }

    // function to format date and time for UI elements
    private fun formatDateTime(initDateTime: String): Array<String> {
        val newDateTime = arrayOf("","")
        val dateSubString = initDateTime.subSequence(0, 10)
        val timeSubString = initDateTime.subSequence(11, 16)
        val toDate = LocalDate.parse(dateSubString)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        newDateTime[0] = toDate.format(formatter)
        newDateTime[1] = timeSubString.toString()
        return newDateTime
    }

    // function to determine whether filterchip should be selectable
    private fun isChipSelectable(option: String): Boolean {
        var selectable = false
        val uiState = _uiState.value
        if (uiState.filteredExams.isNotEmpty()) {
            when (option) {
                in uiState.locations -> {
                    if (option in uiState.locations) {
                        for (x in uiState.filteredExams) {
                            if (option == x.locationname) {
                                selectable = true
                            }
                        }
                    }
                }
                in uiState.candidates -> {
                    for (x in uiState.filteredExams) {
                        if (option == x.candidatename) {
                            selectable = true
                        }
                    }
                }
                in uiState.dates -> {
                    for (x in uiState.filteredExams) {
                        if (option == x.examdate) {
                            selectable = true
                        }
                    }
                }
            }
        } else {
            selectable = true
        }
        return selectable
    }

    // generate filter chips for location options and determine if each is selectable
    @SuppressLint("StateFlowValueCalledInComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GenerateLocationChips() {
        _uiState.value.locations.forEach { option: String ->
            var selected by remember { mutableStateOf(false) }
            val selectable = isChipSelectable(option)
            FilterChip(
                enabled = selectable,
                selected = selected,
                onClick = {
                    selected = !selected
                    onEvent(
                        ExamEvent.Filter(
                            option = option,
                            filterType = "location"
                        )
                    )
                },
                label = { Text(text = option) },
                modifier = Modifier.padding(4.dp)
            )
        }
    }

    // generate filter chips for candidate options and determine if each is selectable
    @SuppressLint("StateFlowValueCalledInComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GenerateCandidateChips() {
        _uiState.value.candidates.forEach { option: String ->
            var selected by remember { mutableStateOf(false) }
            val selectable = isChipSelectable(option)
            FilterChip(
                enabled = selectable,
                selected = selected,
                onClick = {
                    selected = !selected
                    onEvent(
                        ExamEvent.Filter(
                            option = option,
                            filterType = "candidate"
                        )
                    )
                },
                label = { Text(text = option) },
                modifier = Modifier.padding(4.dp),
            )
        }
    }

    // generate filter chips for date options with formatted dates and determine if each is selectable
    @SuppressLint("StateFlowValueCalledInComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GenerateDateChips() {
        _uiState.value.dates.forEach { option: String ->
            var selected by remember { mutableStateOf(false) }
            val selectable = isChipSelectable(option)
            val formattedDateTime = formatDateTime(option)
            FilterChip(
                enabled = selectable,
                selected = selected,
                onClick = {
                    selected = !selected
                    onEvent(
                        ExamEvent.Filter(
                            option = option,
                            filterType = "date"
                        )
                    )
                },
                label = {
                    Text(text = formattedDateTime[0])
                },
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}