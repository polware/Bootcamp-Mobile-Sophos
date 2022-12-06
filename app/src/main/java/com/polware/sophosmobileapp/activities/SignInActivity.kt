package com.polware.sophosmobileapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.polware.sophosmobileapp.databinding.ActivitySignInBinding
import java.util.concurrent.Executor

class SignInActivity : AppCompatActivity() {
    private lateinit var bindingSignIn: ActivitySignInBinding
    private lateinit var mySharedPreferences: SharedPreferences
    // Biometric Auth
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt

    companion object {
        const val PREFERENCES_THEME = "ThemePreferences"
        var SELECTED_THEME = "light_mode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingSignIn = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(bindingSignIn.root)

        getAppTheme()

        bindingSignIn.buttonSignIn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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