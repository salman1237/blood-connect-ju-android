package com.deshlet.bloodconnectju.data.remote

import com.deshlet.bloodconnectju.data.remote.dto.AuthResponse
import com.deshlet.bloodconnectju.data.remote.dto.BloodRequestDto
import com.deshlet.bloodconnectju.data.remote.dto.CreateRequestBody
import com.deshlet.bloodconnectju.data.remote.dto.DonorProfileUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.DonorSummaryDto
import com.deshlet.bloodconnectju.data.remote.dto.LoginRequest
import com.deshlet.bloodconnectju.data.remote.dto.MessageResponse
import com.deshlet.bloodconnectju.data.remote.dto.MetaResponse
import com.deshlet.bloodconnectju.data.remote.dto.RegisterRequest
import com.deshlet.bloodconnectju.data.remote.dto.RequestStatsDto
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("requests")
    suspend fun listRequests(
        @Query("blood_group") bloodGroup: String? = null,
        @Query("hall") hall: String? = null,
    ): Response<List<BloodRequestDto>>

    @GET("requests/stats")
    suspend fun requestStats(): Response<RequestStatsDto>

    @POST("requests")
    suspend fun createRequest(@Body body: CreateRequestBody): Response<BloodRequestDto>

    @GET("requests/{id}")
    suspend fun getRequest(@Path("id") id: Int): Response<BloodRequestDto>

    @GET("requests/{id}/donors")
    suspend fun getMatchingDonors(@Path("id") id: Int): Response<List<DonorSummaryDto>>

    @POST("requests/{id}/respond")
    suspend fun respondToRequest(@Path("id") id: Int): Response<BloodRequestDto>

    @POST("requests/{id}/fulfill")
    suspend fun fulfillRequest(@Path("id") id: Int): Response<BloodRequestDto>

    @PATCH("requests/{requestId}/responses/{responseId}/confirm")
    suspend fun confirmResponse(@Path("requestId") requestId: Int, @Path("responseId") responseId: Int): Response<BloodRequestDto>

    @PATCH("requests/{requestId}/responses/{responseId}/confirm-donation")
    suspend fun confirmDonation(@Path("requestId") requestId: Int, @Path("responseId") responseId: Int): Response<BloodRequestDto>
}
