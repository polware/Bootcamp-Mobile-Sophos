package com.polware.sophosmobileapp.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.data.models.Cities
import com.polware.sophosmobileapp.data.models.DocumentType
import com.polware.sophosmobileapp.databinding.ActivitySendDocumentBinding
import java.io.*
import java.util.*

class SendDocumentActivity : AppCompatActivity(), PermissionRequest.Listener {
    private lateinit var bindingSendDoc: ActivitySendDocumentBinding
    private lateinit var mySharedPreferences: SharedPreferences
    private var activityResultLauncherImageSelected: ActivityResultLauncher<Intent>? = null
    private var activityResultLauncherCameraPhoto: ActivityResultLauncher<Intent>? = null
    private val requestPermissionCamera by lazy {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, 
        Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
    }
    private val requestPermissionStorage by lazy {
        permissionsBuilder(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingSendDoc = ActivitySendDocumentBinding.inflate(layoutInflater)
        setContentView(bindingSendDoc.root)
        setSupportActionBar(bindingSendDoc.toolbarSendDocs)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = resources.getString(R.string.toolbar_title)

        requestPermissionCamera.addListener {
            if (it.allGranted()){
                takePhotoFromCamera()
            }
        }
        requestPermissionStorage.addListener{
            if (it.allGranted()){
                choosePhotoFromGallery()
            }
        }
        registerActivityForImageSelected()
        registerActivityForCameraPhoto()
        setupSpinnersView()

        bindingSendDoc.imageViewPhoto.setOnClickListener {
            customDialogForImage()
        }

    }

    private fun setupSpinnersView(){
        var spinnerAdapter: ArrayAdapter<String>
        mySharedPreferences = getSharedPreferences(SignInActivity.PREFERENCES_THEME, MODE_PRIVATE)
        val value = mySharedPreferences.getString(SignInActivity.SELECTED_THEME, "")
        if (value.equals("light_mode")) {
            // Declare spinner for cities
            spinnerAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, Cities.getCities())
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bindingSendDoc.spinnerCity.adapter = spinnerAdapter
            // Declare spinner for document type
            spinnerAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, DocumentType.getTypes())
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bindingSendDoc.spinnerDocument.adapter = spinnerAdapter
        }
        else {
            spinnerAdapter = ArrayAdapter(applicationContext, R.layout.spinner_item, Cities.getCities())
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bindingSendDoc.spinnerCity.adapter = spinnerAdapter
            spinnerAdapter = ArrayAdapter(applicationContext, R.layout.spinner_item, DocumentType.getTypes())
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bindingSendDoc.spinnerDocument.adapter = spinnerAdapter
        }
    }

    private fun customDialogForImage(){
        val addImageDialog = Dialog(this, R.style.Theme_Dialog)
        addImageDialog.setContentView(R.layout.custom_dialog_add_img)
        val buttonGallery = addImageDialog.findViewById(R.id.textViewGallery) as TextView
        val buttonCamera = addImageDialog.findViewById(R.id.textViewCamera) as TextView
        buttonGallery.setOnClickListener {
            taskForGallery()
            addImageDialog.dismiss()
        }
        buttonCamera.setOnClickListener {
            taskForCamera()
            addImageDialog.dismiss()
        }
        addImageDialog.show()
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

    private fun taskForGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionStorage.send()
        }
        else {
            choosePhotoFromGallery()
        }
    }

    private fun taskForCamera() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionCamera.send()
        }
        else {
            takePhotoFromCamera()
        }
    }

    private fun choosePhotoFromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncherImageSelected?.launch(galleryIntent)
    }

    private fun takePhotoFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activityResultLauncherCameraPhoto?.launch(cameraIntent)
    }

    private fun registerActivityForImageSelected() {
        activityResultLauncherImageSelected = registerForActivityResult(
            ActivityResultContracts
            .StartActivityForResult()) {
                result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == RESULT_OK && data != null) {
                try {
                    val imageuri = data!!.data
                    val inputStream = contentResolver.openInputStream(imageuri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val encodedImage = encodeImageToBase64(bitmap);
                    Log.i("BASE64: ", encodedImage)
                    bindingSendDoc.imageViewPhoto.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@SendDocumentActivity, "Error loading image",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val format64 = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, format64)
        val byteFormat: ByteArray = format64.toByteArray()
        return Base64.encodeToString(byteFormat, Base64.DEFAULT)
    }

    private fun registerActivityForCameraPhoto() {
        activityResultLauncherCameraPhoto = registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()) {
                result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == RESULT_OK) {
                val photo: Bitmap = data!!.extras!!.get("data") as Bitmap
                bindingSendDoc.imageViewPhoto.setImageBitmap(photo)
            }
            else {
                Toast.makeText(this@SendDocumentActivity, "Failed to capture photo",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            result.anyShouldShowRationale() -> showRationalDialogForPermissions()
            result.allGranted() -> Log.i("Permission Result: ", result.toString())
        }
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("You have turned off permission required for" +
                " this feature. It can be enabled under the Applications Settings")
            .setPositiveButton("Go To Settings") {
                    _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") {
                    dialog, _ ->
                dialog.dismiss()
            }
            .show()
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