package com.polware.sophosmobileapp.data.models

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id")
    val userId: String,
    @SerializedName("nombre")
    val name: String,
    @SerializedName("apellido")
    val lastName: String,
    @SerializedName("acceso")
    val access: Boolean,
    val admin: Boolean
    )
