package com.example.victvsschedule.data.remote.dto

import kotlinx.serialization.Serializable

// exam response object from json
@Serializable
data class ExamResponse(
    val id: String,
    val title: String,
    val examdescription: String,
    val examdate: String,
    val candidatename: String,
    val candidateemail: String,
    val locationname: String,
    val latitude: String,
    val longitude: String
)

