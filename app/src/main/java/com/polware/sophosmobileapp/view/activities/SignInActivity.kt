package com.polware.sophosmobileapp.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.data.Constants.LOGIN_PREFERENCES
import com.polware.sophosmobileapp.data.Constants.THEME_PREFERENCES
import com.polware.sophosmobileapp.data.Constants.CURRENT_THEME
import com.polware.sophosmobileapp.databinding.ActivitySignInBinding
import com.polware.sophosmobileapp.view.activities.SignInActivity.LoadSavedCredential.EditTextBindingAdapter
import com.polware.sophosmobileapp.viewmodels.LoginViewModel
import java.util.concurrent.Executor

class SignInActivity: AppCompatActivity() {
    private lateinit var bindingSignIn: ActivitySignInBinding
    private lateinit var mySharedPreferences: SharedPreferences
    private lateinit var viewModel: LoginViewModel
    private lateinit var email: String
    private lateinit var password: String
    // Biometric Auth
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingSignIn = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        getAppTheme()
        setUserCredentials()

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        viewModel.observeUserLiveData().observe(this){ user ->
            val userId = user.userId
            if (userId == "112"){
                val userName = user.name
                savedPreferences(email, password, userName)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else {
                Toast.makeText(this,
                    resources.getString(R.string.message_invalid_emailpassword), Toast.LENGTH_SHORT).show()
            }
        }

        bindingSignIn.buttonSignIn.setOnClickListener {
            email = bindingSignIn.textInputEmail.text.toString()
            password = bindingSignIn.textInputPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                viewModel.validateCredentials(email, password).observe(this) {
                        message ->
                    Toast.makeText(this@SignInActivity, message, Toast.LENGTH_LONG).show() }
                viewModel.getUser()
            }
            else {
                Toast.makeText(this, resources.getString(R.string.message_alert_empty_fields), Toast.LENGTH_SHORT).show()
            }
        }

        bindingSignIn.buttonFingerprint.setOnClickListener {
            fingerPrintAuth()
        }

    }

    private fun getAppTheme() {
        // Save state of app theme using SharedPreferences
        mySharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        val value = mySharedPreferences.getString(CURRENT_THEME, "")
        // Loading theme(dark/light mode) saved when reopen the app
        if (value.equals("dark_mode")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putString(CURRENT_THEME, "dark_mode")
            editor.apply()
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putString(CURRENT_THEME, "light_mode")
            editor.apply()
        }
    }

    private fun setUserCredentials() {
        mySharedPreferences = getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
        val userEmail = mySharedPreferences.getString("Email", "")
        val userPassword = mySharedPreferences.getString("Password", "")
        if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword)) {
            EditTextBindingAdapter(bindingSignIn.textInputEmail, userEmail!!)
            EditTextBindingAdapter(bindingSignIn.textInputPassword, userPassword!!)
        }
    }

    object LoadSavedCredential {
        @JvmStatic @BindingAdapter("android:text")
        fun EditTextBindingAdapter(view: TextInputEditText, value: String?) {
            if (value == null)
                return
            view.setText(value)
        }
    }

    private fun savedPreferences(email: String, password: String, username: String) {
        mySharedPreferences = getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mySharedPreferences.edit()
        editor.putString("Email", email)
        editor.putString("Password", password)
        editor.putString("Username", username)
        editor.apply()
    }

    private fun fingerPrintAuth() {
        if (checkBiometricSensor()){
            executor = ContextCompat.getMainExecutor(this)
            // this will give us result of AUTHENTICATION
            biometricPrompt = BiometricPrompt(this@SignInActivity, executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(applicationContext, "Authentication error: $errString",
                            Toast.LENGTH_SHORT).show()
                    }

                    // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(applicationContext, "Login Success",
                            Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(applicationContext, "Authentication failed",
                            Toast.LENGTH_SHORT).show()
                    }
                })
            // creating variable for Biometric dialog
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build()
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun checkBiometricSensor(): Boolean {
        // Check if user can use biometric sensor
        val biometricManager: BiometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.i("BIOMETRIC", "App can authenticate using biometrics.")
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("BIOMETRIC", "No biometric features available on this device.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("BIOMETRIC", "Biometric features are currently unavailable.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                /*
                // Prompts the user to create credentials that your app accepts
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG)
                }
                startActivityForResult(enrollIntent, 100)
                */
                return false
            }
        }
        return false
    }

}