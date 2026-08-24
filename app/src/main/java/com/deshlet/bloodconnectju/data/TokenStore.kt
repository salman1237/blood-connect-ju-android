package com.deshlet.bloodconnectju.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth")

/**
 * Persists the Sanctum bearer token across app restarts. Also keeps an
 * in-memory copy so the OkHttp auth interceptor (synchronous, runs on every
 * request) doesn't have to block on a DataStore read each time — only the
 * very first read after process start does that.
 */
@Singleton
class TokenStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val tokenKey = stringPreferencesKey("api_token")

    @Volatile
    private var cached: String? = null

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[tokenKey] }

    suspend fun currentToken(): String? {
        val value = cached ?: tokenFlow.first()
        cached = value
        return value
    }

    suspend fun save(token: String) {
        cached = token
        context.dataStore.edit { it[tokenKey] = token }
    }

    suspend fun clear() {
        cached = null
        context.dataStore.edit { it.remove(tokenKey) }
    }
}
