package com.polware.sophosmobileapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.polware.sophosmobileapp.data.Constants.API_KEY
import com.polware.sophosmobileapp.data.Constants.EMAIL
import com.polware.sophosmobileapp.data.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.DocumentItems
import com.polware.sophosmobileapp.data.models.DocumentModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DocumentViewModel: ViewModel() {
    private var documentLiveData = MutableLiveData<List<DocumentItems>>()

    fun getDocumentList() {
        RetrofitBuilder.api.getAllDocuments(API_KEY, EMAIL).enqueue(object :
            Callback<DocumentModel> {
            override fun onResponse(call: Call<DocumentModel>, response: Response<DocumentModel>) {
                if (response.body()!=null){
                    documentLiveData.value = response.body()!!.documentItems
                }
                else {
                    return
                }
            }

            override fun onFailure(call: Call<DocumentModel>, t: Throwable) {
                Log.e("ErrorGettingDocuments: ", t.message.toString())
            }
        })
    }
    fun observeDocumentLiveData() : LiveData<List<DocumentItems>> {
        return documentLiveData
    }
}