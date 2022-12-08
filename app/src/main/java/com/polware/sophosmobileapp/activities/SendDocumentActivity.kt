package com.polware.sophosmobileapp.activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.data.Constants.API_KEY
import com.polware.sophosmobileapp.data.Constants.PREFERENCES_THEME
import com.polware.sophosmobileapp.data.Constants.SELECTED_THEME
import com.polware.sophosmobileapp.data.RetrofitBuilder
import com.polware.sophosmobileapp.data.models.*
import com.polware.sophosmobileapp.databinding.ActivitySendDocumentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.*

class SendDocumentActivity : MainActivity(), PermissionRequest.Listener {
    private lateinit var bindingSendDoc: ActivitySendDocumentBinding
    private lateinit var mySharedPreferences: SharedPreferences
    private var activityResultLauncherImageSelected: ActivityResultLauncher<Intent>? = null
    private var activityResultLauncherCameraPhoto: ActivityResultLauncher<Intent>? = null
    private var encodedImage: String? = null

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

        bindingSendDoc.buttonSendDocument.setOnClickListener {
            validateFormFields()
        }

    }

    private fun setupSpinnersView(){
        var spinnerAdapter: ArrayAdapter<String>
        mySharedPreferences = getSharedPreferences(PREFERENCES_THEME, MODE_PRIVATE)
        val value = mySharedPreferences.getString(SELECTED_THEME, "")
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
                    val imageUri = data.data
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    encodedImage = encodeImageToBase64(bitmap)
                    Log.i("BASE64: ", encodedImage!!)
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

    private fun validateFormFields(){
        when {
            bindingSendDoc.editTextDocNumber.text.isNullOrEmpty() -> {
                Toast.makeText(this, resources.getString(R.string.submit_form_id_warning), Toast.LENGTH_SHORT).show()
            }
            bindingSendDoc.editTextNames.text.isNullOrEmpty() -> {
                Toast.makeText(this, resources.getString(R.string.submit_form_name_warning), Toast.LENGTH_SHORT).show()
            }
            bindingSendDoc.editTextLastName.text.isNullOrEmpty() -> {
                Toast.makeText(this, resources.getString(R.string.submit_form_lastname_warning), Toast.LENGTH_SHORT).show()
            }
            bindingSendDoc.editTextEmailAddress.text.isNullOrEmpty() -> {
                Toast.makeText(this, resources.getString(R.string.submit_form_email_warning), Toast.LENGTH_SHORT).show()
            }
            encodedImage == null -> {
                Toast.makeText(this, resources.getString(R.string.submit_form_image_warning), Toast.LENGTH_SHORT).show()
            }
            else -> {
                saveDocument()
            }
        }
    }

    private fun saveDocument() {
        val id = bindingSendDoc.editTextDocNumber.text.toString()
        val documentType = bindingSendDoc.spinnerDocument.selectedItem.toString()
        val name = bindingSendDoc.editTextNames.text.toString()
        val lastName = bindingSendDoc.editTextLastName.text.toString()
        val city = bindingSendDoc.spinnerCity.selectedItem.toString()
        val email = bindingSendDoc.editTextEmailAddress.text.toString()
        val fileType = encodedImage
        val attachedImage = "Image"
        val newDocument = NewDocument(documentType, id, name, lastName, city,
            email, fileType!!, attachedImage)
        try {
            val newDocumentCall: Call<DocumentItems> = RetrofitBuilder.retrofitService
                .createDocument(API_KEY, newDocument)
            newDocumentCall.enqueue(object : Callback<DocumentItems> {

                override fun onResponse(call: Call<DocumentItems>,
                                        response: Response<DocumentItems>) {
                    val responseCode = response.code().toString()
                    Log.i("ResponseCode: ", responseCode)
                    if (responseCode == "200") {
                        Toast.makeText(this@SendDocumentActivity,
                            "Document added to API", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<DocumentItems>, t: Throwable) {
                    Log.e("ErrorSendingDocument: ", t.message.toString())
                }
            })
        } catch (e: HttpException) {
            Toast.makeText(this, "Connection error!", Toast.LENGTH_SHORT).show()
            Log.e("HttpException: ", "${e.message}")
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

}