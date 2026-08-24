package com.deshlet.bloodconnectju.data.remote

import com.deshlet.bloodconnectju.data.remote.dto.ValidationErrorResponse
import kotlinx.serialization.json.Json

/**
 * Decodes a Laravel ValidationException (422) JSON body — shared by every
 * repository that talks to the API, so this parsing logic exists in one
 * place rather than being copy-pasted per endpoint.
 */
fun parseApiError(json: Json, rawBody: String?): Pair<String, Map<String, List<String>>> {
    if (rawBody.isNullOrBlank()) {
        return "Something went wrong. Please try again." to emptyMap()
    }
    return try {
        val parsed = json.decodeFromString<ValidationErrorResponse>(rawBody)
        parsed.message to parsed.errors
    } catch (e: Exception) {
        "Something went wrong. Please try again." to emptyMap()
    }
}
