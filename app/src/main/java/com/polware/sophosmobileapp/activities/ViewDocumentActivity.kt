package com.polware.sophosmobileapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.adapters.AdapterViewDocument
import com.polware.sophosmobileapp.data.Constants.API_KEY
import com.polware.sophosmobileapp.data.Constants.EMAIL
import com.polware.sophosmobileapp.data.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.DocumentModel
import com.polware.sophosmobileapp.databinding.ActivityViewDocumentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.lang.IllegalArgumentException

class ViewDocumentActivity : MainActivity() {
    private lateinit var bindingViewDoc: ActivityViewDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingViewDoc = ActivityViewDocumentBinding.inflate(layoutInflater)
        setContentView(bindingViewDoc.root)
        setSupportActionBar(bindingViewDoc.toolbarViewDocs)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = resources.getString(R.string.toolbar_title)
        getDocuments()
    }

    private fun getDocuments() {
        try {
            val getDocumentsCall: Call<DocumentModel> = RetrofitBuilder.retrofitService
                .getAllDocuments(API_KEY, EMAIL)
            getDocumentsCall.enqueue(object : Callback<DocumentModel> {
                override fun onResponse(call: Call<DocumentModel>,
                                        response: Response<DocumentModel>) {
                    val responseCode = response.code().toString()
                    val documents: DocumentModel? = response.body()
                    Log.i("ResponseDocuments: ", documents.toString())
                    if (responseCode == "200") {
                        // Load data into the view
                        val documentItems = documents!!.documentItems
                        val adapter = AdapterViewDocument(documentItems,
                            { showImage -> decodeImage(showImage) } )
                        bindingViewDoc.recyclerViewListDocuments.layoutManager =
                            LinearLayoutManager(this@ViewDocumentActivity)
                        bindingViewDoc.recyclerViewListDocuments.adapter = adapter
                        bindingViewDoc.recyclerViewListDocuments.visibility = View.VISIBLE
                        bindingViewDoc.textViewNoRecords.visibility = View.GONE
                    }
                    if (responseCode == "400")
                        Toast.makeText(this@ViewDocumentActivity,
                            "Query not allowed", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<DocumentModel>, t: Throwable) {
                    Log.e("ErrorGettingDocuments: ", t.message.toString())
                }
            })
        } catch (e: HttpException){
            Toast.makeText(this, "Connection error!", Toast.LENGTH_SHORT).show()
            Log.e("HttpException: ", "${e.message}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main,menu)
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

    // Decode base64 string to image
    private fun decodeImage(imageString: String) {
        Log.i("DocumentListener", imageString)
        try {
            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bindingViewDoc.imageViewDocImage.setImageBitmap(decodedImage)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Invalid Base-64 format", Toast.LENGTH_SHORT).show()
            Log.e("Base64Decode", e.message.toString())
        }
    }

}