package com.example.victvsschedule

import com.example.victvsschedule.data.remote.dto.ExamResponse
import com.google.android.gms.maps.model.LatLng

// data class to hold values required for true state of UI
data class UiState(
    // general screen data
    val tabIndex: Int = 0,
    val onListView: Boolean = true,
    val showMapBottomSheet: Boolean = false,

    // list of all returned exams
    val allExams: List<ExamResponse> = emptyList<ExamResponse>().toMutableList(),

    // lists of unique location, candidates and dates
    val locations: List<String> = emptyList<String>().toMutableList(),
    val candidates: List<String> = emptyList<String>().toMutableList(),
    val dates: List<String> = emptyList<String>().toMutableList(),

    // filtered lists
    val filterApplied: Boolean = false,
    val filteredByLocation: List<ExamResponse> = emptyList<ExamResponse>().toMutableList(),
    val filteredByCandidate: List<ExamResponse> = emptyList<ExamResponse>().toMutableList(),
    val filteredByDate: List<ExamResponse> = emptyList<ExamResponse>().toMutableList(),
    val filteredExams: List<ExamResponse> = emptyList<ExamResponse>().toMutableList(),

    // data for currently selected exam
    val examSelected: Boolean = false,
    val selectedId: String? = null,
    val selectedTitle: String? = null,
    val selectedDescription: String? = null,
    val selectedLocation: String? = null,
    val selectedDate: String? = null,
    val selectedTime: String? = null,
    val selectedName: String? = null,
    val selectedEmail: String? = null,
    val selectedLatLng: LatLng? = null
)

