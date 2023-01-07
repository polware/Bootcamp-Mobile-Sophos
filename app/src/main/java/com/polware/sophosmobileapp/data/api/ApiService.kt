package com.polware.sophosmobileapp.data.api

import com.polware.sophosmobileapp.data.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("RS_Usuarios")
    fun getUserInfo(@Query("idUsuario") email: String, @Query("clave") api_key: String): Call<UserModel>

    @GET("RS_Documentos")
    suspend fun getAllDocuments(@Query("appid") app_id: String, @Query("correo") email: String): Response<DocumentModel>

    @POST("RS_Documentos")
    suspend fun sendDocument(@Query("appid") app_id: String,
                     @Body dataDocument: NewDocument): Response<DocumentItems>

    @GET("RS_Oficinas")
    suspend fun getAllOffices(): Response<OfficesModel>

}