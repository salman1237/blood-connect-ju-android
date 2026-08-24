package com.deshlet.bloodconnectju.data

import android.os.Build
import com.deshlet.bloodconnectju.data.remote.ApiService
import com.deshlet.bloodconnectju.data.remote.dto.AuthResponse
import com.deshlet.bloodconnectju.data.remote.dto.LoginRequest
import com.deshlet.bloodconnectju.data.remote.dto.RegisterRequest
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import com.deshlet.bloodconnectju.data.remote.parseApiError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AuthResult {
    data class Success(val user: UserDto) : AuthResult
    data class Failure(val message: String, val fieldErrors: Map<String, List<String>> = emptyMap()) : AuthResult
}

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenStore: TokenStore,
    private val json: Json,
) {
    val isLoggedIn: Flow<Boolean> = tokenStore.tokenFlow.map { !it.isNullOrBlank() }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        role: String,
        gender: String,
        dateOfBirth: String,
    ): AuthResult {
        val request = RegisterRequest(
            name = name,
            email = email,
            password = password,
            password_confirmation = passwordConfirmation,
            role = role,
            gender = gender,
            date_of_birth = dateOfBirth,
            device_name = deviceName(),
        )
        return runAuthCall { api.register(request) }
    }

    suspend fun login(email: String, password: String): AuthResult {
        val request = LoginRequest(email = email, password = password, device_name = deviceName())
        return runAuthCall { api.login(request) }
    }

    /** Best-effort server-side revoke — the local token is cleared either way. */
    suspend fun logout() {
        runCatching { api.logout() }
        tokenStore.clear()
    }

    suspend fun me(): UserDto? = runCatching { api.me() }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    private suspend fun runAuthCall(call: suspend () -> Response<AuthResponse>): AuthResult {
        return try {
            val response = call()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                tokenStore.save(body.token)
                AuthResult.Success(body.user)
            } else {
                parseError(response.errorBody()?.string())
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Couldn't reach the server. Check your connection and try again.")
        }
    }

    private fun parseError(rawBody: String?): AuthResult.Failure {
        val (message, fieldErrors) = parseApiError(json, rawBody)
        return AuthResult.Failure(message, fieldErrors)
    }

    private fun deviceName(): String =
        "${Build.MANUFACTURER} ${Build.MODEL}".trim().ifBlank { "Android device" }
}
