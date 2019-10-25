package com.applicaster.plugin.login.inplayer.data.service

import com.applicaster.plugin.login.inplayer.data.service.RefreshTokenDenied.Reason
import com.applicaster.plugin.login.inplayer.data.service.api.InPlayerApi
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit.MILLISECONDS

interface AuthService {
    fun refreshToken(): Completable
    fun logout(): Completable
    fun isAccessTokenExpired(): Boolean
    fun isRefreshTokenExpired(): Boolean
}

class AuthServiceImpl(val authApi: InPlayerApi, val accountDataProvider: AccountDataProvider) : AuthService {

    companion object {
        private const val ACCESS_TOKEN_VALID_DAYS_PERIOD = 30
        private const val REFRESH_TOKEN_VALID_DAYS_PERIOD = 60
        private const val GRANT_TYPE = "refresh_token"
    }

    override fun isAccessTokenExpired() = accountDataProvider.run {
        refreshToken != null && MILLISECONDS.toDays(currentTimeMillis() - tokenUpdateTime) > ACCESS_TOKEN_VALID_DAYS_PERIOD
    }

    override fun isRefreshTokenExpired() = accountDataProvider.run {
        refreshToken != null && MILLISECONDS.toDays(currentTimeMillis() - tokenUpdateTime) > REFRESH_TOKEN_VALID_DAYS_PERIOD
    }

    override fun refreshToken() = accountDataProvider.refreshToken?.let { refreshToken ->
        authApi.getAccountData()
            .subscribeOn(Schedulers.io())
            .onErrorResumeNext { Single.error(processError(it)) }
            .map { it.merchantUuid }
            .flatMap {
                authApi.refreshToken(GRANT_TYPE, refreshToken, it)
                    .onErrorResumeNext { Single.error(processError(it)) }
            }
            .doOnSuccess {
                accountDataProvider.accessToken = it.accessToken
                accountDataProvider.refreshToken = it.refreshToken
                accountDataProvider.tokenUpdateTime = currentTimeMillis()
            }
            .toCompletable()
    }
        ?: Completable.error(RefreshTokenDenied(Reason.NO_REFRESH_TOKEN))

    override fun logout() = authApi.logout()
        .onErrorResumeNext { Completable.error(processError(it)) }
        .andThen(Completable.fromAction { accountDataProvider.clear() })
        .subscribeOn(Schedulers.io())

    private fun processError(error: Throwable) =
        if (error is HttpException) {
            when (error.code()) {
                401 -> RefreshTokenDenied(Reason.UNAUTORIZED_REQUEST)
                403 -> RefreshTokenDenied(Reason.INVALID_CREDENTIALS)
                404 -> RefreshTokenDenied(Reason.ACCOUNT_NOT_FOUND)
                409 -> RefreshTokenDenied(Reason.NOT_ACTIVE_ACCOUNT)
                422 -> RefreshTokenDenied(Reason.INVALID_PARAMETERS_INPUT)
                else -> NetworkException(error)
            }
        } else NetworkException(error)
}

class RefreshTokenDenied(reason: Reason) : RuntimeException("Refresh token denied because of: $reason") {
    enum class Reason {
        UNAUTORIZED_REQUEST,
        ACCOUNT_NOT_FOUND,
        INVALID_CREDENTIALS,
        NOT_ACTIVE_ACCOUNT,
        INVALID_PARAMETERS_INPUT,
        NO_REFRESH_TOKEN
    }
}

class NetworkException(exception: Throwable) : RuntimeException("Network error occurred", exception)
