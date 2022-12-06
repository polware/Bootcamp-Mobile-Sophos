package com.polware.sophosmobileapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.databinding.ActivityViewDocumentBinding

class ViewDocumentActivity : MainActivity() {
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
            R.id.action_sign_out -> {
                signOut()
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

}