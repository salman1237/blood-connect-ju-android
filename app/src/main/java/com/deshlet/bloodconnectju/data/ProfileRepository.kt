package com.deshlet.bloodconnectju.data

import com.deshlet.bloodconnectju.data.remote.ApiService
import com.deshlet.bloodconnectju.data.remote.dto.DonorProfileUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.MetaResponse
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import com.deshlet.bloodconnectju.data.remote.parseApiError
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ProfileResult {
    data class Success(val user: UserDto) : ProfileResult
    data class Failure(val message: String, val fieldErrors: Map<String, List<String>> = emptyMap()) : ProfileResult
}

/** Backs both the onboarding wizard and the later profile-edit screen — same split as the web app. */
@Singleton
class ProfileRepository @Inject constructor(
    private val api: ApiService,
    private val json: Json,
) {
    suspend fun meta(): MetaResponse? = runCatching { api.meta() }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    suspend fun updateDonorProfile(request: DonorProfileUpdateRequest): ProfileResult {
        return try {
            val response = api.updateDonorProfile(request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ProfileResult.Success(body)
            } else {
                val (message, fieldErrors) = parseApiError(json, response.errorBody()?.string())
                ProfileResult.Failure(message, fieldErrors)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ProfileResult.Failure(e.message ?: "Couldn't reach the server. Check your connection and try again.")
        }
    }
}
