package com.polware.sophosmobileapp.viewmodels

import com.polware.sophosmobileapp.data.models.OfficesModel
import retrofit2.Call

class ApiHelperImpl(private val apiInterface: ApiInterface): ApiHelper {

    override fun getAllOffices(): Call<OfficesModel> = apiInterface.getAllOffices()

}