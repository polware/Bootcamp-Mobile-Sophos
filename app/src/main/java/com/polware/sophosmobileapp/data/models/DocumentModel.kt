package com.polware.sophosmobileapp.data.models

import com.google.gson.annotations.SerializedName

data class DocumentModel(
    @SerializedName("Items")
    val documentItems: List<DocumentItems>
    )
