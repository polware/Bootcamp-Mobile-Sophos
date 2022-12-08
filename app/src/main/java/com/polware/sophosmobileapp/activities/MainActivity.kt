package com.polware.sophosmobileapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.data.Constants.PREFERENCES_THEME
import com.polware.sophosmobileapp.data.Constants.SELECTED_THEME
import com.polware.sophosmobileapp.databinding.ActivityMainBinding
import java.util.*

open class MainActivity : AppCompatActivity() {
    private lateinit var bindingMain: ActivityMainBinding
    private lateinit var mySharedPreferences: SharedPreferences

    companion object {
        lateinit var inactiveLanguage: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)
        setSupportActionBar(bindingMain.toolbarMain)
        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.toolbar_title_main) // Adicionar nombre de usuario

        bindingMain.buttonSend.setOnClickListener {
            startActivity(Intent(this, SendDocumentActivity::class.java))
        }

        bindingMain.buttonViewDoc.setOnClickListener {
            startActivity(Intent(this, ViewDocumentActivity::class.java))
        }

        bindingMain.buttonOffices.setOnClickListener {
            startActivity(Intent(this, OfficesMapActivity::class.java))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        setPopupLanguage(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_send_document -> {
                startActivity(Intent(this, SendDocumentActivity::class.java))
                true
            }
            R.id.action_view_document -> {
                startActivity(Intent(this, ViewDocumentActivity::class.java))
                true
            }
            R.id.action_office_map -> {
                startActivity(Intent(this, OfficesMapActivity::class.java))
                true
            }
            R.id.action_mode_theme -> {
                changeAppTheme()
                true
            }
            R.id.action_language -> {
                changeLanguage(this, inactiveLanguage)
                true
            }
            R.id.action_sign_out -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setPopupLanguage(menu: Menu?){
        val item: MenuItem = menu!!.findItem(R.id.action_language)
        val getAppLanguage = resources.configuration.locale.toString()
        Log.i("APP_LANGUAGE: ", getAppLanguage)
        if (getAppLanguage == "en_US" || getAppLanguage == "en"){
            item.title = resources.getString(R.string.menu_language_spanish)
            inactiveLanguage = "es"
        }
        else {
            item.title = resources.getString(R.string.menu_language_english)
            inactiveLanguage = "en"
        }
    }

    fun changeAppTheme() {
        mySharedPreferences = getSharedPreferences(PREFERENCES_THEME, Context.MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        val themeState = mySharedPreferences.getString(SELECTED_THEME, "")
        if (themeState.equals("dark_mode")) {
            // If dark mode is ON, it will turn off
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putString(SELECTED_THEME, "light_mode")
            editor.apply()
        }
        else {
            // If dark mode is OFF, it will turn on
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putString(SELECTED_THEME, "dark_mode")
            editor.apply()
        }
    }

    fun changeLanguage(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        val refresh = Intent(this, activity::class.java)
        refresh.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(refresh)
    }

    fun signOut() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

}