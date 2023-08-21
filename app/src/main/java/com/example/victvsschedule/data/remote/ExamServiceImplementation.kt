package com.example.victvsschedule.data.remote

import com.example.victvsschedule.data.remote.dto.ExamResponse
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.RedirectResponseException
import io.ktor.client.features.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.url

class ExamsServiceImplementation(
    private val client: HttpClient
) : ExamsService {

    // return list of exam response objects through http route or display exception type if unsuccessful
    override suspend fun getExams(): MutableList<ExamResponse> {
        return try {
            client.get { url(HttpRoutes.EXAMS) }
        } catch(e: RedirectResponseException) {
            println("Error: ${e.response.status.description}")
            return arrayListOf()
        } catch (e: ClientRequestException) {
            println("Error: ${e.response.status.description}")
            return arrayListOf()
        } catch (e: ServerResponseException) {
            println("Error: ${e.response.status.description}")
            return arrayListOf()
        }
    }
}