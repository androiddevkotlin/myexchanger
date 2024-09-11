package com.exchange.app.di

import com.exchange.app.BuildConfig
import com.exchange.app.data.api.CurrencyExchangeApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
class CurrencyExchangeNetworkModule {

    @Provides
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient,
        @BaseUrl baseUrl: String,
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideOkHttpClient(
    ): OkHttpClient = OkHttpClient.Builder()
        .build()

    @Provides
    fun provideCurrencyExchangeApi(retrofit: Retrofit): CurrencyExchangeApi {
        return retrofit.create(CurrencyExchangeApi::class.java)
    }

    @Provides
    @BaseUrl
    fun provideRestApiUrl(): String {
        return BuildConfig.REST_API_URL
    }
}
