package com.polware.sophosmobileapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var bindingMain: ActivityMainBinding

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