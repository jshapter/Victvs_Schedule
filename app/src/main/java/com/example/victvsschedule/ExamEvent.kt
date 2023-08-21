package com.example.victvsschedule

sealed interface ExamEvent {

    // select tab event required for selecting new tab through means other than tab onClick handler
    data class SelectTab(
        val index: Int
    ): ExamEvent

    // use exam id to update UI for selected exam
    data class SelectForMap(
        val id: String
    ): ExamEvent

    // invoke filtering algorithm and update UI
    data class Filter(
        val option: String,
        val filterType: String
    ): ExamEvent
}