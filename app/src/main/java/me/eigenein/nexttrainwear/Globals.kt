package me.eigenein.nexttrainwear

import me.eigenein.nexttrainwear.api.*
import me.eigenein.nexttrainwear.utils.Cache
import me.eigenein.nexttrainwear.utils.registryMatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.*

class Globals {
    companion object {
        val NS_API = Retrofit.Builder()
            .baseUrl("http://webservices.ns.nl/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create(Persister(registryMatcher {
                bind(Date::class.java, DateFormatTransformer::class.java)
                bind(JourneyOptionStatus::class.java, JourneyOptionStatusTransformer::class.java)
            })))
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
            )
            .build()
            .create(NsApiService::class.java)!!

        val JOURNEY_OPTIONS_RESPONSE_CACHE = Cache<String, JourneyOptionsResponse>()
    }
}

