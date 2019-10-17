package com.applicaster.plugin.login.inplayer

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.applicaster.plugin.login.inplayer.data.config.ConfigProviderImpl
import com.applicaster.plugin.login.inplayer.data.service.AccountDataProviderImpl
import com.applicaster.plugin.login.inplayer.data.service.AuthServiceImpl
import com.applicaster.plugin_manager.hook.HookListener
import com.applicaster.plugin_manager.login.LoginContract
import com.applicaster.plugin_manager.playersmanager.Playable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class InplayerLoginContract : LoginContract {

    companion object {
        const val REFRESH_TOKEN_KEY = "refresh_token_key"

        private val configProvider by lazy { ConfigProviderImpl() }
        private val accountDataProvider by lazy { AccountDataProviderImpl() }
        private val authService by lazy { AuthServiceImpl(accountDataProvider) }
    }

    private var loginCallback: LoginContract.Callback? = null

    @SuppressLint("CheckResult")
    override fun executeOnStartup(context: Context, listener: HookListener) {
        Timber.d("executeOnStartup")
        listener.onHookFinished()
    }

    override fun setPluginConfigurationParams(params: MutableMap<Any?, Any?>) {
        Timber.d("Plugin params: $params")
        val loginUrl = params["login_url"] as? String
        configProvider.loginUrl = Uri.parse(loginUrl).buildUpon().run {
            appendQueryParameter("platform", "android")
            appendQueryParameter("redirect_uri", "redirect_uri")
            build().toString()
        }
        Timber.d("Plugin configProvider: $configProvider")
    }

    override fun login(context: Context, additionalParams: Map<*, *>, callback: LoginContract.Callback?) {
        this.loginCallback = callback
        InplayerLoginActivity.launch(
            context = context, params = InplayerLoginActivity.Params(
                loginUrl = configProvider.loginUrl
            )
        )
    }

    override fun login(
        context: Context, playable: Playable, additionalParams: Map<*, *>, callback: LoginContract.Callback
    ) {
        this.loginCallback = callback
        InplayerLoginActivity.launch(
            context = context, params = InplayerLoginActivity.Params(
                loginUrl = configProvider.loginUrl
            )
        )
        Timber.d("playable :: $playable additionalParams :: $additionalParams")
    }

    @SuppressLint("CheckResult")
    override fun logout(context: Context, additionalParams: Map<*, *>, callback: LoginContract.Callback?) {
        Timber.d("logout additionalParams = $additionalParams")
        authService.logout()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("Logout successfully finished")
                callback?.onResult(true)
            }, {
                Timber.w(it, "Logout finished with error")
                callback?.onResult(false)
            })

    }

    override fun isTokenValid(): Boolean {
        Timber.d("isTokenValid call")
        return !accountDataProvider.accessToken.isNullOrEmpty()
    }

    override fun getToken(): String? {
        val token = accountDataProvider.accessToken
        Timber.d("getToken() $token")
        return token
    }

    override fun setToken(token: String?) {
        Timber.d("set token = $token")
        accountDataProvider.accessToken = token
        if (!token.isNullOrEmpty()) {
            loginCallback?.onResult(true)
        }
    }

    override fun setUserData(userData: MutableMap<Any?, Any?>) {
        val refreshToken = userData[REFRESH_TOKEN_KEY] as? String
        refreshToken?.let {
            accountDataProvider.refreshToken = refreshToken
            accountDataProvider.tokenUpdateTime = System.currentTimeMillis()
        }
    }

    override fun handlePluginScheme(context: Context, data: Map<String, String>): Boolean {
        Timber.d("handlePluginScheme context :: $context data :: $data")
        if ("login" == data["type"]) {
            if (!data.containsKey("action")) {
                return false
            }
            return when (data["action"]) {
                "login" -> {
                    login(context, data, null)
                    true
                }
                "logout" -> {
                    logout(context, data, null)
                    true
                }
                else -> false
            }
        }
        return false
    }

    override fun executeOnApplicationReady(context: Context, listener: HookListener) {
        Timber.d("executeOnApplicationReady context :: $context listener :: $listener")
    }

    override fun isItemLocked(model: Any) = false
}
