package zlc.season.rxdownload3.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private val FAKE_BASE_URL = "http://www.example.com"

    fun get(baseUrl: String = FAKE_BASE_URL): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private fun okHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)

        val httpLogger = HttpLoggingInterceptor()
        httpLogger.level = HttpLoggingInterceptor.Level.NONE

//        builder.addInterceptor(httpLogger)

        return builder.build()
    }
}