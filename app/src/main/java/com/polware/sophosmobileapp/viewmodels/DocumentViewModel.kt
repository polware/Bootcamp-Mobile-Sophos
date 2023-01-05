package com.polware.sophosmobileapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.polware.sophosmobileapp.data.Constants.API_KEY
import com.polware.sophosmobileapp.data.Constants.EMAIL
import com.polware.sophosmobileapp.data.api.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.DocumentItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DocumentViewModel: ViewModel() {
    private var documentLiveData = MutableLiveData<List<DocumentItems>>()

    fun getDocumentList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitBuilder.api.getAllDocuments(API_KEY, EMAIL)
                Log.i("DocumentViewModel", response.body().toString())
                documentLiveData.postValue(response.body()!!.documentItems)
            }
            catch (e: HttpException) {
                Log.e("HttpException: ", "${e.message}")
            }
        }
    }

    fun observeDocumentLiveData(): LiveData<List<DocumentItems>> {
        return documentLiveData
    }
}