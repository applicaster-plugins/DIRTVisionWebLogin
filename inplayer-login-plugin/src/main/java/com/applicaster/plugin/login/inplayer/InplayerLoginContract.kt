package com.applicaster.plugin.login.inplayer

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.applicaster.atom.model.APAtomEntry
import com.applicaster.plugin.login.inplayer.data.config.ConfigProviderImpl
import com.applicaster.plugin.login.inplayer.data.service.AccountDataProviderImpl
import com.applicaster.plugin.login.inplayer.data.service.AuthServiceImpl
import com.applicaster.plugin_manager.hook.HookListener
import com.applicaster.plugin_manager.login.LoginContract
import com.applicaster.plugin_manager.login.LoginContract.Callback
import com.applicaster.plugin_manager.playersmanager.Playable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class InplayerLoginContract : LoginContract {

    companion object {
        const val REFRESH_TOKEN_KEY = "refresh_token_key"
        const val REDIRECT_URI_KEY = "redirect_uri"
        const val REDIRECT_URI_VALUE = "https://www.app.app"

        private val configProvider by lazy { ConfigProviderImpl() }
        private val accountDataProvider by lazy { AccountDataProviderImpl() }
        private val authService by lazy { AuthServiceImpl(accountDataProvider) }
    }

    init {
        if (Timber.treeCount() == 0) Timber.plant(Timber.DebugTree())
    }

    private var loginCallback: Callback? = null

    override fun executeOnStartup(context: Context, listener: HookListener) {
        Timber.d("executeOnStartup")
        listener.onHookFinished()
    }

    override fun setPluginConfigurationParams(params: MutableMap<Any?, Any?>) {
        Timber.d("Plugin params: $params")
        val loginUrl = params["login_url"] as? String
        configProvider.loginUrl = Uri.parse(loginUrl).buildUpon().run {
            appendQueryParameter("platform", "android")
            appendQueryParameter(REDIRECT_URI_KEY, REDIRECT_URI_VALUE)
            build().toString()
        }
    }

    override fun login(context: Context, additionalParams: Map<*, *>?, callback: Callback?) {
        login(context, null, additionalParams, callback)
    }

    override fun login(context: Context, playable: Playable?, additionalParams: Map<*, *>?, callback: Callback?) {
        Timber.d("login playable :: $playable additionalParams :: $additionalParams")
        this.loginCallback = callback
        InplayerLoginActivity.launch(
            context = context, params = InplayerLoginActivity.Params(
                loginUrl = configProvider.loginUrl
            )
        )
    }

    @SuppressLint("CheckResult")
    override fun logout(context: Context, additionalParams: Map<*, *>, callback: Callback?) {
        Timber.d("logout additionalParams = $additionalParams")
        authService.logout()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback?.onResult(true)
            }, {
                callback?.onResult(false)
            })

    }

    override fun isItemLocked(model: Any): Boolean {
        Timber.d("isItemLocked")
        return if (model is APAtomEntry.APAtomEntryPlayable) {
            model.isFree.not() && !isTokenValid
        } else {
            false
        }
    }

    override fun isItemLocked(context: Context?, model: Any, callback: Callback) {
        callback.onResult(isItemLocked(model))
    }

    override fun isTokenValid(): Boolean {
        Timber.d("isTokenValid token = $accountDataProvider.accessToken")
        return !accountDataProvider.accessToken.isNullOrEmpty() && !authService.isAccessTokenExpired()
    }

    override fun getToken() = accountDataProvider.accessToken

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
        Timber.d("executeOnApplicationReady context ::  $context listener :: $listener")
        listener.onHookFinished()
    }
}
