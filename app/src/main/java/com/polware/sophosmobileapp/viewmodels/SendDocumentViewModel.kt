package com.polware.sophosmobileapp.viewmodels

import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.polware.sophosmobileapp.data.models.NewDocument
import com.polware.sophosmobileapp.view.activities.SendDocumentActivity.Companion.encodedImageB64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendDocumentViewModel(private val sendDocumentRepository: SendDocumentRepository): ViewModel() {
    val inputId = MutableLiveData<String>()
    val inputDocument = MutableLiveData<String>()
    val inputName = MutableLiveData<String>()
    val inputLastName = MutableLiveData<String>()
    val inputCity = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()
    private val inputImage = MutableLiveData<String?>()
    val inputFileType = MutableLiveData<String>()
    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> get() = statusMessage

    val status: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(sendDocumentRepository.sendStatus) { flag ->
            value = flag
        }
    }

    val documentListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            inputDocument.postValue(parent!!.getItemAtPosition(position) as String)
        }
    }

    val cityListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selected = parent!!.getItemAtPosition(position) as String
            Log.i("Spinner: ", selected)
            inputCity.postValue(parent.getItemAtPosition(position) as String)
        }
    }

    fun saveNewDocument() {
        inputImage.value = encodedImageB64
        if (inputImage.value == null) {
            statusMessage.value = Event("Please select an image")
        }
        else if (inputId.value == null) {
            statusMessage.value = Event("Please enter a ID")
        }
        else if (inputName.value == null) {
            statusMessage.value = Event("Please enter a name")
        }
        else if (inputLastName.value == null) {
            statusMessage.value = Event("Please enter a lastname")
        }
        else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter a email")
        }
        else if (inputFileType.value == null) {
            statusMessage.value = Event("Please enter image description")
        }
        else {
            val id = inputId.value
            val documentType = inputDocument.value
            val name = inputName.value
            val lastName = inputLastName.value
            val city = inputCity.value
            val email = inputEmail.value
            val attachedImage = inputImage.value
            val fileType = inputFileType.value
            CoroutineScope(Dispatchers.IO).launch {
                sendDocumentRepository.saveNewDocument(NewDocument(documentType!!, id!!, name!!,
                    lastName!!, city!!, email!!, attachedImage!!, fileType!!))
            }
        }
    }

}