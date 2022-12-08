package com.polware.sophosmobileapp.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.polware.sophosmobileapp.data.Constants.LOGIN_PREFERENCES
import com.polware.sophosmobileapp.data.Constants.PREFERENCES_THEME
import com.polware.sophosmobileapp.data.Constants.SELECTED_THEME
import com.polware.sophosmobileapp.databinding.ActivitySignInBinding
import java.util.concurrent.Executor

class SignInActivity : AppCompatActivity() {
    private lateinit var bindingSignIn: ActivitySignInBinding
    private lateinit var mySharedPreferences: SharedPreferences
    // Biometric Auth
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingSignIn = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(bindingSignIn.root)

        getAppTheme()
        setUserCredentials()

        bindingSignIn.buttonSignIn.setOnClickListener {
            val email = bindingSignIn.textInputEmail.text.toString()
            val password = bindingSignIn.textInputPassword.text.toString()
            if(validateTextFields(email, password)){
                savedPreferences(email, password)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        bindingSignIn.buttonFingerprint.setOnClickListener {
            fingerPrintAuth()
        }

    }

    private fun getAppTheme() {
        // Save state of app theme using SharedPreferences
        mySharedPreferences = getSharedPreferences(PREFERENCES_THEME, MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        val value = mySharedPreferences.getString(SELECTED_THEME, "")
        // Loading theme(dark/light mode) saved when reopen the app
        if (value.equals("dark_mode")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putString(SELECTED_THEME, "dark_mode")
            editor.apply()
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putString(SELECTED_THEME, "light_mode")
            editor.apply()
        }
    }

    private fun validateTextFields(email: String, password: String): Boolean {
        return if (!validEmail(email)) {
            Toast.makeText(this, "The email entered does not have a valid format",
                Toast.LENGTH_LONG).show()
            false
        } else if (!validPassword(password)) {
            Toast.makeText(this, "Password must be at least 4 characters",
                Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    private fun validEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validPassword(password: String): Boolean {
        return password.length >= 4
    }

    private fun setUserCredentials() {
        mySharedPreferences = getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
        val email = mySharedPreferences.getString("Email", "")
        val password = mySharedPreferences.getString("Password", "")
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            bindingSignIn.textInputEmail.setText(email)
            bindingSignIn.textInputPassword.setText(password)
        }
    }

    private fun savedPreferences(email: String, password: String) {
        mySharedPreferences = getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mySharedPreferences.edit()
        editor.putString("Email", email)
        editor.putString("Password", password)
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