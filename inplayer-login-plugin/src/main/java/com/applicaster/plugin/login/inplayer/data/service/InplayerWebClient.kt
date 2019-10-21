package com.applicaster.plugin.login.inplayer.data.service

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.applicaster.plugin.login.inplayer.InplayerLoginContract
import com.applicaster.plugin_manager.login.LoginManager
import timber.log.Timber

class InplayerWebClient(private val callback: UpdateCallback) : WebViewClient() {

    // Uses just for API < 21
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        Timber.i("shouldOverrideUrlLoading < 21 $url")
        return parseToken(Uri.parse(url))
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Timber.i("shouldOverrideUrlLoading >= 21 " + request.url)
            return parseToken(request.url)
        }
        return super.shouldOverrideUrlLoading(view, request)// return true to cancel loading
    }

    private fun parseToken(url: Uri): Boolean {
        val authToken = url.getQueryParameter(TOKEN_KEY)
        val refreshToken = url.getQueryParameter(REFRESH_TOKEN_KEY)
        if (authToken != null) {
            if (authToken.isEmpty()) {
                callback.onUpdate(State.ERROR)
            } else {
                LoginManager.getLoginPlugin().token = authToken
                LoginManager.getLoginPlugin().userData = mapOf(InplayerLoginContract.REFRESH_TOKEN_KEY to refreshToken)
                return true
            }
        }
        return false
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        callback.onUpdate(State.LOADING)
        Timber.i("onPageStarted $url")
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        Timber.i("onPageFinished $url")
        if (!LoginManager.getLoginPlugin().token.isNullOrEmpty()) {
            callback.onUpdate(State.SUCCESS)
        }
    }

    companion object {
        private val TOKEN_KEY = "inplayer_token"
        private val REFRESH_TOKEN_KEY = "inplayer_refresh_token"
        private val STREAM_TOKEN_KEY = "stream_token"
    }
}
