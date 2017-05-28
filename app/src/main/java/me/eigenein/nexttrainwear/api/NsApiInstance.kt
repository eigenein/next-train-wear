package me.eigenein.nexttrainwear.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

val NsApiInstance = Retrofit.Builder()
    .baseUrl("http://webservices.ns.nl/")
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(SimpleXmlConverterFactory.create(Persister(ApiUtils.matcher)))
    .client(OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
    )
    .build()
    .create(NsApiService::class.java)!!
