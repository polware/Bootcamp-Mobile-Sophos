package com.polware.sophosmobileapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.polware.sophosmobileapp.data.Constants
import com.polware.sophosmobileapp.data.api.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.DocumentItems
import com.polware.sophosmobileapp.data.models.NewDocument
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendDocumentViewModel(private val newDocument: NewDocument): ViewModel() {
    var status = MutableLiveData<Boolean?>()

    fun saveNewDocument(){
        RetrofitBuilder.api.createDocument(Constants.API_KEY, newDocument).enqueue(object :
            Callback<DocumentItems> {
            override fun onResponse(call: Call<DocumentItems>, response: Response<DocumentItems>) {
                val responseCode = response.code().toString()
                Log.i("ResponseCode: ", responseCode)
                if (responseCode == "200") {
                    // At network successfully response
                    status.value = true
                }
            }

            override fun onFailure(call: Call<DocumentItems>, t: Throwable) {
                Log.e("ErrorSendingDocument: ", t.message.toString())
            }
        })
    }

}