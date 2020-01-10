package com.applicaster.plugin.login.inplayer.sample

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.applicaster.plugin_manager.login.LoginManager
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

/**
 * This sample activity will create a player contract which will create an instance of your player
 * and pass it a sample playable item. From there you have options to launch full screen or attach
 * inline.
 * This is for testing your implementation against the Zapp plugin system.
 *
 *
 * Note: You must have your player plugin module in this project and the appropriate plugin
 * manifest
 * in the plugin_configurations.json
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //
        Timber.plant(Timber.DebugTree())

        login_button.setOnClickListener {
            LoginManager.getLoginPlugin().login(this, mutableMapOf<Any?, Any?>()) {
                Toast.makeText(this, "Fresh Login completed", Toast.LENGTH_SHORT).show()
            }
        }
        logout_button.setOnClickListener {
            LoginManager.getLoginPlugin().logout(this, mutableMapOf<Any?, Any?>()) {
                Toast.makeText(this, "Logout completed", Toast.LENGTH_SHORT).show()
            }
        }
        show_token.setOnClickListener {
            Toast.makeText(this, "Token is ${LoginManager.getLoginPlugin().token}", Toast.LENGTH_SHORT).show()
        }
        executeOnStartup.setOnClickListener {
            LoginManager.getLoginPlugin().executeOnStartup(
                this,
                { Toast.makeText(this, "Execution on startup finished", Toast.LENGTH_SHORT).show() })
        }
    }
}
