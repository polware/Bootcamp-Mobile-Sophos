package com.polware.sophosmobileapp.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.polware.sophosmobileapp.data.Constants
import com.polware.sophosmobileapp.data.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.OfficesModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OfficesMapViewModel(private val mySharedPreferences: SharedPreferences): ViewModel() {
    private var officesList: OfficesModel? = null

    fun getOfficesList() {
        RetrofitBuilder.api.getAllOffices().enqueue(object : Callback<OfficesModel> {
            override fun onResponse(call: Call<OfficesModel>, response: Response<OfficesModel>) {
                if (response.isSuccessful) {
                    officesList = response.body()
                    Log.i("OfficesViewModel", "$officesList")
                    // Storing data in the Shared Preferences
                    val responseJsonString = Gson().toJson(officesList)
                    val editor = mySharedPreferences.edit()
                    editor.putString(Constants.OFFICES_LOCATION, responseJsonString)
                    editor.apply()
                }
                else {
                    return
                }
            }

            override fun onFailure(call: Call<OfficesModel>, t: Throwable) {
                Log.e("ResponseError: ", t.message.toString())
            }
        })
    }
}