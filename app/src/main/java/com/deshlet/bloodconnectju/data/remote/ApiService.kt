package com.deshlet.bloodconnectju.data.remote

import com.deshlet.bloodconnectju.data.remote.dto.AccountUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.AuthResponse
import com.deshlet.bloodconnectju.data.remote.dto.BloodRequestDto
import com.deshlet.bloodconnectju.data.remote.dto.CreateRequestBody
import com.deshlet.bloodconnectju.data.remote.dto.DeleteAccountRequest
import com.deshlet.bloodconnectju.data.remote.dto.DonorDetailDto
import com.deshlet.bloodconnectju.data.remote.dto.DonorProfileUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.DonorSummaryDto
import com.deshlet.bloodconnectju.data.remote.dto.LeaderboardEntryDto
import com.deshlet.bloodconnectju.data.remote.dto.LoginRequest
import com.deshlet.bloodconnectju.data.remote.dto.MessageResponse
import com.deshlet.bloodconnectju.data.remote.dto.MetaResponse
import com.deshlet.bloodconnectju.data.remote.dto.RegisterPushTokenRequest
import com.deshlet.bloodconnectju.data.remote.dto.RegisterRequest
import com.deshlet.bloodconnectju.data.remote.dto.RequestStatsDto
import com.deshlet.bloodconnectju.data.remote.dto.UnregisterPushTokenRequest
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
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

    @PATCH("profile")
    suspend fun updateAccount(@Body body: AccountUpdateRequest): Response<UserDto>

    // Retrofit's @DELETE annotation rejects an @Body parameter outright
    // ("Non-body HTTP method cannot contain @Body") — found live, on-device,
    // via the account-deletion flow throwing that exact exception. @HTTP
    // with an explicit hasBody=true is the documented way to get a DELETE
    // request with a body past Retrofit's annotation processor.
    @HTTP(method = "DELETE", path = "profile", hasBody = true)
    suspend fun deleteAccount(@Body body: DeleteAccountRequest): Response<Unit>

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

    @POST("push-tokens")
    suspend fun registerPushToken(@Body body: RegisterPushTokenRequest): Response<Unit>

    // Same @DELETE-can't-have-@Body issue as deleteAccount() above — this
    // one had been silently failing since Phase 4b (unregisterCurrentToken()
    // swallows the exception via runCatching, so logout still "worked" from
    // the user's perspective, just without ever actually revoking the
    // device's push token). Found and fixed together with deleteAccount().
    @HTTP(method = "DELETE", path = "push-tokens", hasBody = true)
    suspend fun unregisterPushToken(@Body body: UnregisterPushTokenRequest): Response<Unit>

    @GET("donors")
    suspend fun listDonors(
        @Query("search") search: String? = null,
        @Query("blood_group") bloodGroup: String? = null,
        @Query("hall") hall: String? = null,
    ): Response<List<DonorSummaryDto>>

    @GET("donors/{id}")
    suspend fun getDonor(@Path("id") id: Int): Response<DonorDetailDto>

    @GET("leaderboard")
    suspend fun getLeaderboard(): Response<List<LeaderboardEntryDto>>
}
