package com.polware.sophosmobileapp.data.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class LoginInfo: BaseObservable() {

    var userName: String? = null
        @Bindable get
        set(userName) {
            field = userName
            notifyPropertyChanged(BR.viewModelLogin)
        }

    var userPassWord: String? = null
        @Bindable get
        set(userPassWord) {
            field = userPassWord
            notifyPropertyChanged(BR.viewModelLogin)
        }
}