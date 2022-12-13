package com.polware.sophosmobileapp.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.regex.Pattern

/**
 * This repository validates the format of the email and password fields
 */
class ValidationRepository(application: Application) {
    var application: Application
    var validEmail: Boolean = false

    init {
        this.application = application
    }

    fun validateCredentials(emailID: String, password: String): LiveData<String> {
        val loginErrorMessage = MutableLiveData<String>()
        if(isEmailValid(emailID)){
            if(password.length<4 && !isPasswordValid(password)){
                loginErrorMessage.value = "Password must have at least 4 characters"
            } else{
                //loginErrorMessage.value = "Successful Login"
                validEmail = true
            }
        }
        else{
            loginErrorMessage.value = "Email format is invalid"
        }
        return  loginErrorMessage
    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun isPasswordValid(password: String): Boolean{
        val expression  ="^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\\\S+\$).{4,}\$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

}