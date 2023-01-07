package com.polware.sophosmobileapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class SendDocViewModelFactory(private val sendDocumentRepository: SendDocumentRepository):
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if(modelClass.isAssignableFrom(SendDocumentViewModel::class.java)){
            return SendDocumentViewModel(sendDocumentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}