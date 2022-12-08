package com.polware.sophosmobileapp.data.models

import com.google.gson.annotations.SerializedName

data class DocumentItems (
    @SerializedName("IdRegistro")
    val id: String,
    @SerializedName("Fecha")
    val date: String,
    @SerializedName("TipoId")
    val docType: String,
    @SerializedName("Identificacion")
    val document: String,
    @SerializedName("Nombre")
    val name: String,
    @SerializedName("Apellido")
    val lastName: String,
    @SerializedName("Ciudad")
    val city: String,
    @SerializedName("Correo")
    val email: String,
    @SerializedName("TipoAdjunto")
    val fileType: String,
    @SerializedName("Adjunto")
    val attachedFile: String
    )