package com.polware.sophosmobileapp.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.data.models.OfficesModel
import com.polware.sophosmobileapp.data.RetrofitBuilder
import com.polware.sophosmobileapp.databinding.ActivityOfficesMapBinding
import com.polware.sophosmobileapp.fragments.MapFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OfficesMapActivity : AppCompatActivity(){
    private lateinit var bindingOfficesMap: ActivityOfficesMapBinding
    private val USER_LOCATION_REQUEST_CODE = 100
    var currentFragment: Fragment? = null
    private var officesList: OfficesModel? = null
    private lateinit var mySharedPreferences: SharedPreferences

    companion object {
        var OFFICES_LOCATION = "bundle_offices_location"
        const val PREFERENCES_NAME = "OfficesPreference"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingOfficesMap = ActivityOfficesMapBinding.inflate(layoutInflater)
        setContentView(bindingOfficesMap.root)
        setSupportActionBar(bindingOfficesMap.toolbarMap)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = resources.getString(R.string.toolbar_title)

        mySharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        getOfficeList()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        }
        else{
            currentFragment = MapFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, currentFragment as MapFragment).commit()
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

    private fun getOfficeList() {
        val officesCall: Call<OfficesModel> = RetrofitBuilder.retrofitService.getAllOffices()
        officesCall.enqueue(object: Callback<OfficesModel> {
            override fun onResponse(call: Call<OfficesModel>, response: Response<OfficesModel>) {
                if (response.isSuccessful) {
                    officesList = response.body()
                    Log.i("ResponseResult", "$officesList")
                    // Storing data in the SharedPreferences
                    val responseJsonString = Gson().toJson(officesList)
                    val editor = mySharedPreferences.edit()
                    editor.putString(OFFICES_LOCATION, responseJsonString)
                    editor.apply()
                }
                else {
                    // If the response is not success then check the response code
                    when (response.code()) {
                        400 -> {
                            Log.e("Error 400", "Bad Request")
                        }
                        404 -> {
                            Log.e("Error 404", "Not Found")
                        }
                        else -> {
                            Log.e("Error", "Generic Error")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<OfficesModel>, t: Throwable) {
                Log.e("ResponseError: ", t.message.toString())
            }
        })
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                USER_LOCATION_REQUEST_CODE)
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == USER_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                currentFragment = MapFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, currentFragment as MapFragment).commit()
            }
            else {
                Toast.makeText(this,
                    "By rejecting the location permission you cannot see the map of the offices",
                    Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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