package com.polware.sophosmobileapp.data.models

import com.google.gson.annotations.SerializedName

data class OfficesModel(
    @SerializedName("Items")
    val CityItems: List<CityItems>
    )