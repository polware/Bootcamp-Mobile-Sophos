package com.polware.sophosmobileapp.viewmodels

import com.polware.sophosmobileapp.data.models.OfficesModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("RS_Oficinas")
    fun getAllOffices(): Call<OfficesModel>

}