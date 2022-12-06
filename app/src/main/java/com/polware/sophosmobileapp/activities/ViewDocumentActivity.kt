package com.polware.sophosmobileapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.databinding.ActivityViewDocumentBinding
import java.util.*

class ViewDocumentActivity : AppCompatActivity() {
    private lateinit var bindingViewDoc: ActivityViewDocumentBinding
    private lateinit var mySharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingViewDoc = ActivityViewDocumentBinding.inflate(layoutInflater)
        setContentView(bindingViewDoc.root)
        setSupportActionBar(bindingViewDoc.toolbarViewDocs)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = resources.getString(R.string.toolbar_title)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main,menu)
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
                val addImageDialog = AlertDialog.Builder(this)
                addImageDialog.setTitle(resources.getString(R.string.dialog_language_title))
                val addImageOptions = arrayOf(resources.getString(R.string.dialog_language_english),
                    resources.getString(R.string.dialog_language_spanish))
                addImageDialog.setItems(addImageOptions) {
                        _, which ->
                    when(which){
                        0 -> changeLanguage(this, "en")
                        1 -> changeLanguage(this, "es")
                    }
                }
                addImageDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Decode base64 string to image
    private fun decodeImage(imageString: String) {
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        bindingViewDoc.imageViewShowDocument.setImageBitmap(decodedImage)
    }

    private fun changeAppTheme() {
        mySharedPreferences = getSharedPreferences(SignInActivity.PREFERENCES_THEME, Context.MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        val themeState = mySharedPreferences.getString(SignInActivity.SELECTED_THEME, "")
        if (themeState.equals("dark_mode")) {
            // If dark mode is ON, it will turn off
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putString(SignInActivity.SELECTED_THEME, "light_mode")
            editor.apply()
        }
        else {
            // If dark mode is OFF, it will turn on
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putString(SignInActivity.SELECTED_THEME, "dark_mode")
            editor.apply()
        }
    }

    private fun changeLanguage(activity: Activity, languageCode: String) {
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

}