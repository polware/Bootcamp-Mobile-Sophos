package com.polware.sophosmobileapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.polware.sophosmobileapp.data.api.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.OfficesModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OfficesMapViewModel: ViewModel() {
    private var officesLiveData = MutableLiveData<OfficesModel>()

    fun getOfficesList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitBuilder.api.getAllOffices()
                Log.i("OfficesViewModel", response.body().toString())
                officesLiveData.postValue(response.body())
            }
            catch (e: HttpException) {
                Log.e("HttpException: ", "${e.message}")
            }
        }
    }

    fun observeOfficesList(): LiveData<OfficesModel> {
        return officesLiveData
    }
}