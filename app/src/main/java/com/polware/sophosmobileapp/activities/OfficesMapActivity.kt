package com.polware.sophosmobileapp.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.data.Constants.OFFICES_LOCATION
import com.polware.sophosmobileapp.data.Constants.PREFERENCES_NAME
import com.polware.sophosmobileapp.data.models.OfficesModel
import com.polware.sophosmobileapp.data.RetrofitBuilder
import com.polware.sophosmobileapp.databinding.ActivityOfficesMapBinding
import com.polware.sophosmobileapp.fragments.MapFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class OfficesMapActivity : MainActivity(){
    private lateinit var bindingOfficesMap: ActivityOfficesMapBinding
    private val USER_LOCATION_REQUEST_CODE = 100
    var currentFragment: Fragment? = null
    private var officesList: OfficesModel? = null
    private lateinit var mySharedPreferences: SharedPreferences

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

    private fun getOfficeList() {
        try {
            val officesCall: Call<OfficesModel> = RetrofitBuilder.retrofitService.getAllOffices()
            officesCall.enqueue(object: Callback<OfficesModel> {

                override fun onResponse(call: Call<OfficesModel>, response: Response<OfficesModel>) {
                    if (response.isSuccessful) {
                        officesList = response.body()
                        Log.i("ResponseResult", "$officesList")
                        // Storing data in the Shared Preferences
                        val responseJsonString = Gson().toJson(officesList)
                        val editor = mySharedPreferences.edit()
                        editor.putString(OFFICES_LOCATION, responseJsonString)
                        editor.apply()
                    }
                }

                override fun onFailure(call: Call<OfficesModel>, t: Throwable) {
                    Log.e("ResponseError: ", t.message.toString())
                }
            })
        } catch (e: HttpException) {
            Toast.makeText(this, "Connection error!", Toast.LENGTH_SHORT).show()
            Log.e("HttpException: ", "${e.message}")
        }

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

}