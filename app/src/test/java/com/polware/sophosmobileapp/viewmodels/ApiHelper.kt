package com.polware.sophosmobileapp.viewmodels

import com.polware.sophosmobileapp.data.models.OfficesModel
import retrofit2.Call

interface ApiHelper {

    fun getAllOffices(): Call<OfficesModel>

}