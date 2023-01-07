package com.polware.sophosmobileapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.polware.sophosmobileapp.data.Constants
import com.polware.sophosmobileapp.data.api.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.NewDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendDocumentRepository {
    private val _sendStatus = MutableLiveData<Boolean>()
    val sendStatus: LiveData<Boolean> get() = _sendStatus
    var responseCode = "0"

    suspend fun saveNewDocument(newDocument: NewDocument) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitBuilder.api.sendDocument(Constants.API_KEY, newDocument)
                responseCode = response.code().toString()
                Log.i("ResponseCode: ", responseCode)
                if (responseCode == "200") {
                    _sendStatus.postValue(true)
                }
            }
            catch (e: Exception) {
                Log.e("ExceptionOnResponse: ", "${e.message}")
                _sendStatus.postValue(false)
            }
        }
    }

}