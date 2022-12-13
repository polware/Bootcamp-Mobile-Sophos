package com.polware.sophosmobileapp.data.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

/**
 * This model class class is linked to layout "activity_sign_in"
 */
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