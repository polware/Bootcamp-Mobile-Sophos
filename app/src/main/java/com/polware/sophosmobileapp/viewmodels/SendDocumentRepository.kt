package com.polware.sophosmobileapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.polware.sophosmobileapp.data.Constants
import com.polware.sophosmobileapp.data.api.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.DocumentItems
import com.polware.sophosmobileapp.data.models.NewDocument
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendDocumentRepository {
    var sendStatus = MutableLiveData<Boolean>()
    var responseCode = "0"

    fun saveNewDocument(newDocument: NewDocument){
        RetrofitBuilder.api.createDocument(Constants.API_KEY, newDocument).enqueue(object :
            Callback<DocumentItems> {
            override fun onResponse(call: Call<DocumentItems>, response: Response<DocumentItems>) {
                responseCode = response.code().toString()
                Log.i("ResponseCode: ", responseCode)
                if (responseCode == "200") {
                    sendStatus.value = true
                    validateResponse()
                }
            }

            override fun onFailure(call: Call<DocumentItems>, t: Throwable) {
                Log.e("ErrorSendingDocument: ", t.message.toString())
                sendStatus.value = false
                validateResponse()
            }
        })
    }

    fun validateResponse(): Boolean {
        val boolean = sendStatus.value
        return boolean == true
    }

}