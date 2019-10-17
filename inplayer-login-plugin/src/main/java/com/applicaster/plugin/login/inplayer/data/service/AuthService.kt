package com.applicaster.plugin.login.inplayer.data.service

import io.reactivex.Completable

interface AuthService {
    fun logout(): Completable
}

class AuthServiceImpl(val accountDataProvider: AccountDataProvider) : AuthService {

    override fun logout() = Completable.fromAction { accountDataProvider.clear() }
}
