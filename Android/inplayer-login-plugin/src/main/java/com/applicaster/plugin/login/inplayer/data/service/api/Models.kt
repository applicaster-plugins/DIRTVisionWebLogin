package com.applicaster.plugin.login.inplayer.data.service.api

import com.google.gson.annotations.SerializedName

data class AccountDataResponse(
    @SerializedName("merchant_uuid") val merchantUuid: String
)

data class RefreshTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String
)

