package com.polware.sophosmobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class OfficeViewModelFactory(private val sharedPreferences: SharedPreferences): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return OfficesMapViewModel(sharedPreferences) as T
    }
}