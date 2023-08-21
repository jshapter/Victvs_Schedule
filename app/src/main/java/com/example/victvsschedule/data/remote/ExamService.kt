package com.example.victvsschedule.data.remote

import com.example.victvsschedule.data.remote.dto.ExamResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging

interface ExamsService {

    // return list of exam response objects
    suspend fun getExams(): List<ExamResponse>

    // create http client, install logging and serialization plugins
    companion object {
        fun create(): ExamsService {
            return ExamsServiceImplementation(
                client = HttpClient(Android) {
                    install(Logging)
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                }
            )
        }
    }
}