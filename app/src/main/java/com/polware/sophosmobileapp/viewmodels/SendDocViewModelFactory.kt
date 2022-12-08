package com.polware.sophosmobileapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.polware.sophosmobileapp.data.models.NewDocument

class SendDocViewModelFactory(private val newDocument: NewDocument): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return SendDocumentViewModel(newDocument) as T
    }
}