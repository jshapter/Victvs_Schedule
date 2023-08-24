package com.example.victvsschedule.data.remote

// new data class to add more appropriate date/time formatting and property naming
data class Exam(
    val id: String,
    val title: String,
    val examDescription: String,
    val examDateTime: String,
    val formattedExamDate: String,
    val formattedExamTime: String,
    val candidateName: String,
    val candidateEmail: String,
    val locationName: String,
    val latitude: String,
    val longitude: String
)
