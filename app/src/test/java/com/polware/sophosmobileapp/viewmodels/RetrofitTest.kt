package com.polware.sophosmobileapp.resources

import com.polware.sophosmobileapp.viewmodels.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitTest {

    private const val BASE_URL = "https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com/"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiInterface: ApiInterface = getRetrofit().create(ApiInterface::class.java)
}