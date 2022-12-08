package com.polware.sophosmobileapp.data

import com.polware.sophosmobileapp.data.models.DocumentItems
import com.polware.sophosmobileapp.data.models.DocumentModel
import com.polware.sophosmobileapp.data.models.NewDocument
import com.polware.sophosmobileapp.data.models.OfficesModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("RS_Oficinas")
    fun getAllOffices(): Call<OfficesModel>

    @GET("RS_Documentos")
    fun getAllDocuments(@Query("appid") app_id: String, @Query("correo") email: String): Call<DocumentModel>

    @POST("RS_Documentos")
    fun createDocument(@Query("appid") app_id: String,
                       @Body dataDocument: NewDocument): Call<DocumentItems>

}