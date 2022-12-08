package com.polware.sophosmobileapp.view.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.view.adapters.AdapterViewDocument
import com.polware.sophosmobileapp.databinding.ActivityViewDocumentBinding
import com.polware.sophosmobileapp.viewmodels.DocumentViewModel
import java.lang.IllegalArgumentException

class ViewDocumentActivity : MainActivity() {
    private lateinit var bindingViewDoc: ActivityViewDocumentBinding
    private lateinit var viewModel: DocumentViewModel
    private lateinit var adapterDocument : AdapterViewDocument

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingViewDoc = ActivityViewDocumentBinding.inflate(layoutInflater)
        setContentView(bindingViewDoc.root)
        setSupportActionBar(bindingViewDoc.toolbarViewDocs)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = resources.getString(R.string.toolbar_title)

        setupRecyclerView()
        viewModel = ViewModelProvider(this)[DocumentViewModel::class.java]
        viewModel.getDocumentList()
        viewModel.observeDocumentLiveData().observe(this) { documents ->
            adapterDocument.setDocumentList(documents)
        }
    }

    private fun setupRecyclerView() {
        adapterDocument = AdapterViewDocument { showImage -> decodeImage(showImage) }
        bindingViewDoc.recyclerViewListDocuments.apply {
            layoutManager = LinearLayoutManager(this@ViewDocumentActivity)
            adapter = adapterDocument
            bindingViewDoc.recyclerViewListDocuments.visibility = View.VISIBLE
            bindingViewDoc.textViewNoRecords.visibility = View.GONE
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