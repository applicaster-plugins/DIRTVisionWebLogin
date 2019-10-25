package com.applicaster.plugin.login.inplayer.data.service

import com.applicaster.plugin.login.inplayer.data.config.ConfigProvider
import com.applicaster.plugin.login.inplayer.data.service.api.InPlayerApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

interface ApiFactory {
    val authApi: InPlayerApi
}

class ApiFactoryImpl(
    private val configProvider: ConfigProvider,
    private val accountDataProvider: AccountDataProvider
) : ApiFactory {

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor {
                val original = it.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer ${accountDataProvider.accessToken}")
                    .method(original.method(), original.body())
                    .build()

                it.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val loggingInterceptor
        get() = HttpLoggingInterceptor { Timber.d(it) }.setLevel(HttpLoggingInterceptor.Level.BODY)

    private val converter by lazy { GsonConverterFactory.create() }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(configProvider.inplayerBaseUrl)
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(converter)
            .build()
    }

    override val authApi: InPlayerApi by lazy {
        retrofit
            .create(InPlayerApi::class.java)
    }
}
