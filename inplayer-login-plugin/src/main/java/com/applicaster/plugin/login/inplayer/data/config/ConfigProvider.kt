package com.applicaster.plugin.login.inplayer.data.config

interface ConfigProvider {
    val loginUrl: String
}

data class ConfigProviderImpl(override var loginUrl: String = "") : ConfigProvider
