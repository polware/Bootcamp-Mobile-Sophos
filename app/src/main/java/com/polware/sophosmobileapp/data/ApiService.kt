package com.polware.sophosmobileapp.data

import com.polware.sophosmobileapp.data.models.DocumentModel
import com.polware.sophosmobileapp.data.models.OfficesModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("RS_Oficinas")
    fun getAllOffices(): Call<OfficesModel>

    @GET("RS_Documentos")
    fun getAllDocuments(): Call<DocumentModel>

    @POST("RS_Documentos")
    fun createDocument(@Body dataDocument: DocumentModel): Call<DocumentModel>

}