package com.polware.sophosmobileapp.data.models

import com.google.gson.annotations.SerializedName

data class Items(
    @SerializedName("IdOficina")
    val idOffice: Int,
    @SerializedName("Ciudad")
    val city: String,
    @SerializedName("Nombre")
    val name: String,
    @SerializedName("Latitud")
    val latitude: String,
    @SerializedName("Longitud")
    val longitude: String
    )