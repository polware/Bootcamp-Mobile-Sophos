package com.polware.sophosmobileapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.polware.sophosmobileapp.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var bindingSignIn: ActivitySignInBinding
    private lateinit var mySharedPreferences: SharedPreferences

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

    }

    private fun getAppTheme() {
        // Save state of app theme using SharedPreferences
        mySharedPreferences = getSharedPreferences(PREFERENCES_THEME, MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        val value = mySharedPreferences.getString(SELECTED_THEME, "")
        // Loading theme(dark/light mode) saved when reopen the app
        if (value.equals("dark_mode")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putString(SELECTED_THEME, "dark_mode")
            editor.apply()
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putString(SELECTED_THEME, "light_mode")
            editor.apply()
        }
    }

}