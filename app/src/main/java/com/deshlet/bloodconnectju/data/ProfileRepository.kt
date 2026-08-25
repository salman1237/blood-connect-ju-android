package com.deshlet.bloodconnectju.data

import android.content.Context
import android.net.Uri
import com.deshlet.bloodconnectju.data.remote.ApiService
import com.deshlet.bloodconnectju.data.remote.dto.AccountUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.DeleteAccountRequest
import com.deshlet.bloodconnectju.data.remote.dto.DonorProfileUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.MetaResponse
import com.deshlet.bloodconnectju.data.remote.dto.UpdateNotificationSettingsRequest
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import com.deshlet.bloodconnectju.data.remote.parseApiError
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ProfileResult {
    data class Success(val user: UserDto) : ProfileResult
    data class Failure(val message: String, val fieldErrors: Map<String, List<String>> = emptyMap()) : ProfileResult
}

sealed interface DeleteAccountResult {
    data object Success : DeleteAccountResult
    data class Failure(val message: String, val fieldErrors: Map<String, List<String>> = emptyMap()) : DeleteAccountResult
}

/** Backs both the onboarding wizard and the later profile-edit screen — same split as the web app. */
@Singleton
class ProfileRepository @Inject constructor(
    private val api: ApiService,
    private val json: Json,
    @param:ApplicationContext private val context: Context,
) {
    suspend fun meta(): MetaResponse? = runCatching { api.meta() }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    suspend fun updateDonorProfile(request: DonorProfileUpdateRequest): ProfileResult =
        runProfileCall { api.updateDonorProfile(request) }

    suspend fun updateAccount(name: String, email: String): ProfileResult =
        runProfileCall { api.updateAccount(AccountUpdateRequest(name = name, email = email)) }

    /** Mirrors web's SettingsController::updateNotifications() — just the email-notifications toggle. */
    suspend fun updateNotificationSettings(enabled: Boolean): ProfileResult =
        runProfileCall { api.updateNotificationSettings(UpdateNotificationSettingsRequest(email_notifications_enabled = enabled)) }

    /**
     * Mirrors web's upload-photo form (image, up to 4MB, validated
     * server-side too) — `uri` comes from the system Photo Picker, which
     * hands back a content:// Uri rather than a real file path, so the
     * bytes are read through ContentResolver rather than File(uri.path).
     */
    suspend fun uploadPhoto(uri: Uri): ProfileResult {
        val part = try {
            contentUriToPart(uri)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        } ?: return ProfileResult.Failure("Couldn't read the selected photo.")

        return runProfileCall { api.uploadPhoto(part) }
    }

    suspend fun removePhoto(): ProfileResult = runProfileCall { api.deletePhoto() }

    private fun contentUriToPart(uri: Uri): MultipartBody.Part? {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(uri) ?: "image/jpeg"
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("photo", "photo.jpg", requestBody)
    }

    suspend fun deleteAccount(password: String): DeleteAccountResult {
        return try {
            val response = api.deleteAccount(DeleteAccountRequest(password = password))
            if (response.isSuccessful) {
                DeleteAccountResult.Success
            } else {
                val (message, fieldErrors) = parseApiError(json, response.errorBody()?.string())
                DeleteAccountResult.Failure(message, fieldErrors)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            DeleteAccountResult.Failure(e.message ?: "Couldn't reach the server. Check your connection and try again.")
        }
    }

    /** Shared by updateDonorProfile/updateAccount — same try/catch/parseApiError shape, one place to keep it. */
    private suspend fun runProfileCall(call: suspend () -> Response<UserDto>): ProfileResult {
        return try {
            val response = call()
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
