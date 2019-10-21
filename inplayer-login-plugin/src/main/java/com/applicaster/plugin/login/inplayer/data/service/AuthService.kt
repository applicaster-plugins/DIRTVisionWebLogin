package com.applicaster.plugin.login.inplayer.data.service

import io.reactivex.Completable
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit

interface AuthService {
    fun logout(): Completable
    fun isAccessTokenExpired(): Boolean
}

class AuthServiceImpl(val accountDataProvider: AccountDataProvider) : AuthService {

    companion object {
        private const val ACCESS_TOKEN_VALID_DAYS_PERIOD = 30
        private const val REFRESH_TOKEN_VALID_DAYS_PERIOD = 60
    }

    override fun isAccessTokenExpired() = accountDataProvider.run {
        refreshToken != null && TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis() - tokenUpdateTime) > ACCESS_TOKEN_VALID_DAYS_PERIOD
    }

    override fun logout() = Completable.fromAction { accountDataProvider.clear() }
}
