package io.kinescope.sdk.network

import io.kinescope.sdk.api.KinescopeVideoApi
import io.kinescope.sdk.utils.accessToken
import io.kinescope.sdk.utils.kinescopeApiEndpoint
import io.kinescope.sdk.utils.tokenType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private fun getOkhttpClient() = OkHttpClient.Builder()
        //.addInterceptor (HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor (Interceptor { chain ->
            var request: Request = chain.request()
            request = request.newBuilder().header("Authorization", "$tokenType $accessToken").build()
            chain.proceed(request)
        }).build()

    private fun getRetrofit() : Retrofit = Retrofit.Builder()
        .client(getOkhttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(kinescopeApiEndpoint)
        .build()




    fun getVideoApi() : KinescopeVideoApi = getRetrofit().create(KinescopeVideoApi::class.java)
}