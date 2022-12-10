package com.polware.sophosmobileapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.polware.sophosmobileapp.data.Constants
import com.polware.sophosmobileapp.data.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(): AndroidViewModel(Application()) {
    private var userLiveData = MutableLiveData<UserModel>()
    private lateinit var validationRepository: ValidationRepository
    private lateinit var userEmail: String

    constructor(application: Application) : this() {
        validationRepository = ValidationRepository(application)
    }

    fun validateCredentials(email: String, password: String): LiveData<String> {
        userEmail = email
        return validationRepository.validateCredentials(email,password)
    }

    fun getUser(){
        if (validationRepository.validEmail){
            Log.i("EmailR: ", userEmail)
            RetrofitBuilder.api.getUserInfo(userEmail, Constants.API_KEY).enqueue(object :
                Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if (response.isSuccessful){
                        Log.i("UserInfo: ", response.body().toString())
                        userLiveData.value = response.body()
                    }
                    else {
                        return
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    Log.e("ErrorGettingUserData: ", t.message.toString())
                }
            })
        }
    }

    fun observeUserLiveData(): LiveData<UserModel> {
        return userLiveData
    }

}