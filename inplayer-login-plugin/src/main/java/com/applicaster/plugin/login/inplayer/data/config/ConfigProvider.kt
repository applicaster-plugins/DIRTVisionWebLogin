package com.applicaster.plugin.login.inplayer.data.config

interface ConfigProvider {
    val loginUrl: String
    val inplayerBaseUrl: String
}

data class ConfigProviderImpl(override var loginUrl: String = "", override var inplayerBaseUrl: String = "") :
    ConfigProvider
