package com.applicaster.plugin.login.inplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import com.applicaster.plugin.login.inplayer.data.service.InplayerWebClient
import com.applicaster.plugin.login.inplayer.data.service.State
import com.applicaster.plugin.login.inplayer.data.service.UpdateCallback
import com.applicaster.plugin_manager.login.LoginManager
import io.reactivex.disposables.Disposable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

/**
 * Inplayer Login Activity with auth functionality implemented via WebView. Don't launch manually.
 * @see InplayerLoginContract for usage
 */
class InplayerLoginActivity : AppCompatActivity() {

    @Parcelize
    data class Params(val loginUrl: String) : Parcelable

    companion object {

        fun launch(context: Context, params: Params) {
            val intent = Intent(context, InplayerLoginActivity::class.java).apply {
                putExtra(EXTRA_PARAMS, params)
            }
            context.startActivity(intent)
        }

        private const val EXTRA_PARAMS = "inplayer_login_activity_params"
    }

    private var loginDisposable: Disposable? = null
    private var loginResult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        val params = intent.extras.getParcelable<Params>(EXTRA_PARAMS)

        webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = InplayerWebClient(object : UpdateCallback {
                override fun onUpdate(state: State) {
                    Timber.d("Web client status $state")
                    when (state) {
                        State.SUCCESS -> finish()
                    }
                }
            })
            loadUrl(params?.loginUrl)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LoginManager.notifyEvent(this, LoginManager.RequestType.CLOSED, loginResult)
        loginDisposable = loginDisposable?.run { dispose(); null }
    }
}
