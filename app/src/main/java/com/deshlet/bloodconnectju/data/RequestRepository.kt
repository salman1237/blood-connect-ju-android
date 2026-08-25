package com.deshlet.bloodconnectju.data

import com.deshlet.bloodconnectju.data.remote.ApiService
import com.deshlet.bloodconnectju.data.remote.dto.BloodRequestDto
import com.deshlet.bloodconnectju.data.remote.dto.CreateRequestBody
import com.deshlet.bloodconnectju.data.remote.dto.DonorSummaryDto
import com.deshlet.bloodconnectju.data.remote.dto.RequestStatsDto
import com.deshlet.bloodconnectju.data.remote.parseApiError
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

sealed interface RequestResult {
    data class Success(val request: BloodRequestDto) : RequestResult
    data class Failure(val message: String, val fieldErrors: Map<String, List<String>> = emptyMap()) : RequestResult
}

@Singleton
class RequestRepository @Inject constructor(
    private val api: ApiService,
    private val json: Json,
) {
    suspend fun listRequests(bloodGroup: String? = null, hall: String? = null): List<BloodRequestDto>? =
        runCatching { api.listRequests(bloodGroup, hall) }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    suspend fun stats(): RequestStatsDto? =
        runCatching { api.requestStats() }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    suspend fun listMine(): List<BloodRequestDto>? =
        runCatching { api.listMyRequests() }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    suspend fun get(id: Int): BloodRequestDto? =
        runCatching { api.getRequest(id) }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    suspend fun matchingDonors(id: Int): List<DonorSummaryDto>? =
        runCatching { api.getMatchingDonors(id) }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    suspend fun create(body: CreateRequestBody): RequestResult =
        runAction { api.createRequest(body) }

    suspend fun respond(requestId: Int): RequestResult =
        runAction { api.respondToRequest(requestId) }

    suspend fun fulfill(requestId: Int): RequestResult =
        runAction { api.fulfillRequest(requestId) }

    suspend fun confirmResponse(requestId: Int, responseId: Int): RequestResult =
        runAction { api.confirmResponse(requestId, responseId) }

    suspend fun confirmDonation(requestId: Int, responseId: Int): RequestResult =
        runAction { api.confirmDonation(requestId, responseId) }

    /** Shared by every action above — same try/catch/parseApiError shape, one place to keep it. */
    private suspend fun runAction(call: suspend () -> Response<BloodRequestDto>): RequestResult {
        return try {
            val response = call()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                RequestResult.Success(body)
            } else {
                val (message, fieldErrors) = parseApiError(json, response.errorBody()?.string())
                RequestResult.Failure(message, fieldErrors)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            RequestResult.Failure(e.message ?: "Couldn't reach the server. Check your connection and try again.")
        }
    }
}
