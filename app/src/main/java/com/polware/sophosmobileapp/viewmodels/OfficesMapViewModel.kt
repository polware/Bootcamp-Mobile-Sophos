package com.polware.sophosmobileapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.polware.sophosmobileapp.data.api.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.OfficesModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OfficesMapViewModel: ViewModel() {
    private var officesLiveData = MutableLiveData<OfficesModel>()

    fun getOfficesList() {
        RetrofitBuilder.api.getAllOffices().enqueue(object : Callback<OfficesModel> {
            override fun onResponse(call: Call<OfficesModel>, response: Response<OfficesModel>) {
                if (response.isSuccessful) {
                    Log.i("OfficesViewModel", response.body().toString())
                    officesLiveData.value = response.body()
                }
            }

            override fun onFailure(call: Call<OfficesModel>, t: Throwable) {
                Log.e("ResponseError: ", t.message.toString())
            }
        })
    }

    fun observeOfficesList(): LiveData<OfficesModel> {
        return officesLiveData
    }
}