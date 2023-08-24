package com.example.victvsschedule.ui

import com.example.victvsschedule.data.remote.Exam
import com.google.android.gms.maps.model.LatLng

// data class to hold values required for true state of UI
data class UiState(
    // general screen data
    val tabIndex: Int = 0,
    val onListView: Boolean = true,
    val showMapBottomSheet: Boolean = false,

    // list of all returned exams
    val allExams: List<Exam> = emptyList<Exam>().toMutableList(),

    // lists of unique location, candidates and dates
    val locations: List<String> = emptyList<String>().toMutableList(),
    val candidates: List<String> = emptyList<String>().toMutableList(),
    val dates: List<String> = emptyList<String>().toMutableList(),

    // filtered lists
    val filterApplied: Boolean = false,
    val filteredByLocation: List<Exam> = emptyList<Exam>().toMutableList(),
    val filteredByCandidate: List<Exam> = emptyList<Exam>().toMutableList(),
    val filteredByDate: List<Exam> = emptyList<Exam>().toMutableList(),
    val filteredExams: List<Exam> = emptyList<Exam>().toMutableList(),

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

