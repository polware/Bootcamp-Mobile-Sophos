package com.polware.sophosmobileapp.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.view.activities.OfficesMapActivity
import com.polware.sophosmobileapp.data.Constants.OFFICES_LOCATION
import com.polware.sophosmobileapp.data.Constants.PREFERENCES_NAME
import com.polware.sophosmobileapp.data.models.OfficesModel

class MapFragment: Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var mapView: MapView? = null
    private var rootView: View? = null
    private lateinit var mySharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = rootView!!.findViewById(R.id.map)
        if (mapView != null) {
            mapView!!.onCreate(null)
            mapView!!.onResume()
            mapView!!.getMapAsync(this)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(gMap: GoogleMap) {
        mMap = gMap
        gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        // Reading offices location from SharedPreferences
        mySharedPreferences = (activity as OfficesMapActivity).getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val officeSharedPreferences = mySharedPreferences.getString(OFFICES_LOCATION, "")
        val officesList = Gson().fromJson(officeSharedPreferences, OfficesModel::class.java)
        for (index in officesList.CityItems.indices){
            val latitude = officesList.CityItems[index].latitude.toDouble()
            val longitude = officesList.CityItems[index].longitude.toDouble()
            val office = LatLng(latitude, longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(office, 12f))
            mMap.animateCamera(CameraUpdateFactory.zoomIn())
            val camera = CameraPosition.Builder()
                    .target(office)
                    .zoom(12f)
                    .tilt(30f)
                    .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
            val bitmapIcon = BitmapFactory.decodeResource(resources, R.mipmap.icon_location)
            mMap.addMarker(
            MarkerOptions().position(office).title("Sede ${officesList.CityItems[index].city}")
                        .snippet(officesList.CityItems[index].name).draggable(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon)))
            }
        pointToUserLocation()
    }

    @SuppressLint("MissingPermission")
    private fun pointToUserLocation(){
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mCriteria = Criteria()
        val bestProvider = locationManager.getBestProvider(mCriteria, true).toString()
        val mLocation = locationManager.getLastKnownLocation(bestProvider)
        if (mLocation != null) {
            val currentLatitude = mLocation.latitude
            Log.i("Current Latitude: ", "$currentLatitude")
            val currentLongitude = mLocation.longitude
            Log.i("Current Longitude: ", "$currentLongitude")
            val userLocation = LatLng(currentLatitude, currentLongitude)
            mMap.addMarker(MarkerOptions().position(userLocation)
                .title(resources.getString(R.string.map_title_marker)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12f), 2000, null)
        }
    }

}