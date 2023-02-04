package io.kinescope.sdk.network

import com.squareup.moshi.Moshi
import io.kinescope.sdk.BuildConfig
import io.kinescope.sdk.api.KinescopeApi
import io.kinescope.sdk.api.KinescopeVideoApi
import io.kinescope.sdk.utils.accessToken
import io.kinescope.sdk.utils.kinescopeApiEndpoint
import io.kinescope.sdk.utils.tokenType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitBuilder {
    private fun getMoshi(): Moshi {
        return  Moshi.Builder()
            //.addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private fun getOkhttpClient(): OkHttpClient.Builder  {
        val httpClientBulder: OkHttpClient.Builder = OkHttpClient.Builder()

        httpClientBulder.addInterceptor (Interceptor { chain ->
            var request: Request = chain.request()
            request = request.newBuilder().header("Authorization", "$tokenType $accessToken").build()
            chain.proceed(request)
        })

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
            httpClientBulder.addInterceptor(interceptor)
        }

        return httpClientBulder
    }


    private fun getRetrofit() : Retrofit = Retrofit.Builder()
        .client(getOkhttpClient().build())
        .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
        .baseUrl(kinescopeApiEndpoint)
        .build()


    fun getVideoApi() : KinescopeVideoApi = getRetrofit().create(KinescopeVideoApi::class.java)
    fun getKinescopeApi() : KinescopeApi = getRetrofit().create(KinescopeApi::class.java)
}