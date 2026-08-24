package com.deshlet.bloodconnectju.data.remote

import com.deshlet.bloodconnectju.data.remote.dto.AuthResponse
import com.deshlet.bloodconnectju.data.remote.dto.DonorProfileUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.LoginRequest
import com.deshlet.bloodconnectju.data.remote.dto.MessageResponse
import com.deshlet.bloodconnectju.data.remote.dto.MetaResponse
import com.deshlet.bloodconnectju.data.remote.dto.RegisterRequest
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

/** Mirrors routes/api.php's /api/v1 group on the Laravel backend. */
interface ApiService {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(): Response<MessageResponse>

    @GET("user")
    suspend fun me(): Response<UserDto>

    @GET("meta")
    suspend fun meta(): Response<MetaResponse>

    @PATCH("donor-profile")
    suspend fun updateDonorProfile(@Body body: DonorProfileUpdateRequest): Response<UserDto>
}
