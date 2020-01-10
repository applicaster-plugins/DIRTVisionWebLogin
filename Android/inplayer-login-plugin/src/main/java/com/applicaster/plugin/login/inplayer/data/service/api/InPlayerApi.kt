package com.applicaster.plugin.login.inplayer.data.service.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface InPlayerApi {

    @FormUrlEncoded
    @POST("accounts/authenticate")
    fun refreshToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String
    ): Single<RefreshTokenResponse>

    @GET("accounts")
    fun getAccountData(): Single<AccountDataResponse>

    @GET("accounts/logout")
    fun logout(): Completable
}
