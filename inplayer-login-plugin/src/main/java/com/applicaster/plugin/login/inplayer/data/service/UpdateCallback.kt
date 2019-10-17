package com.applicaster.plugin.login.inplayer.data.service

interface UpdateCallback {
    fun onUpdate(state: State)
}

enum class State { LOADING, SUCCESS, ERROR }

