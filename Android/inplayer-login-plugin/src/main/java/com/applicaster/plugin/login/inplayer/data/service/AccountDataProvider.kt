package com.applicaster.plugin.login.inplayer.data.service

import com.applicaster.util.PreferenceUtil

interface AccountDataProvider {
    var accessToken: String?
    var refreshToken: String?
    var tokenUpdateTime: Long
    fun clear()
}

class AccountDataProviderImpl : AccountDataProvider {
    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token_key_inplayer"
        private const val REFRESH_TOKEN_KEY = "refresh_token_key_inplayer"
        private const val TOKEN_UPDATE_TIME_KEY = "token_update_time_key_inplayer"
        private const val PREFERENCES_NAME = "inplayer_login_plugin_account_prefs"
    }

    private val prefs = PreferenceUtil.getInstance(PREFERENCES_NAME)

    override var accessToken: String?
        get() = prefs.getStringPref(ACCESS_TOKEN_KEY, null)
        set(value) {
            prefs.setStringPref(ACCESS_TOKEN_KEY, value)
        }

    override var refreshToken: String?
        get() = prefs.getStringPref(REFRESH_TOKEN_KEY, null)
        set(value) {
            prefs.setStringPref(REFRESH_TOKEN_KEY, value)
        }

    override var tokenUpdateTime: Long
        get() = prefs.getLongPref(TOKEN_UPDATE_TIME_KEY, 0)
        set(value) {
            prefs.setLongPref(TOKEN_UPDATE_TIME_KEY, value)
        }

    override fun clear() {
        accessToken = null
        refreshToken = null
        tokenUpdateTime = 0
    }
}
