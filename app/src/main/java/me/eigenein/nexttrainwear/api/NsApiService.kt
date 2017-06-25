package me.eigenein.nexttrainwear.api

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NsApiService {
    @Headers("Authorization:Basic ZWlnZW5laW5AZ21haWwuY29tOm90bGw3Nm15TDRFZmpMUGlPOHhxNnplNHVXUzRQNTl1VUZXX21TRVpIS0lIOFdTeWlTTlg0dw==")
    @GET("ns-api-treinplanner")
    fun trainPlanner(
        @Query("fromStation") fromStation: String,
        @Query("toStation") toStation: String,
        @Query("previousAdvices") previousAdvices: Int = 5,
        @Query("nextAdvices") nextAdvices: Int = 5
    ): Flowable<JourneyOptionsResponse>
}
