package com.polware.sophosmobileapp.data

import com.polware.sophosmobileapp.data.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com/"

object RetrofitBuilder {
    val retrofitService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Create Retrofit client
        return@lazy retrofit.create(ApiService::class.java)
    }
}