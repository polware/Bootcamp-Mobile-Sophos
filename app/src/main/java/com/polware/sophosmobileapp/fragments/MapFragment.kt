package com.polware.sophosmobileapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Bundle
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
import com.polware.sophosmobileapp.activities.OfficesMapActivity
import com.polware.sophosmobileapp.activities.OfficesMapActivity.Companion.OFFICES_LOCATION
import com.polware.sophosmobileapp.activities.OfficesMapActivity.Companion.PREFERENCES_NAME
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.data.models.OfficesModel

class MapFragment: Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var locationManager: LocationManager? = null
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
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mMap.isMyLocationEnabled = true;
        mMap.uiSettings.isZoomControlsEnabled = true;
        mMap.uiSettings.isCompassEnabled = true;
        // Reading offices location from SharedPreferences
        mySharedPreferences = (activity as OfficesMapActivity).getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val officeSharedPreferences = mySharedPreferences.getString(OFFICES_LOCATION, "")
        val officesList = Gson().fromJson(officeSharedPreferences, OfficesModel::class.java)
        for (index in officesList.Items!!.indices){
            val latitude = officesList.Items[index].latitude.toDouble()
            val longitude = officesList.Items[index].longitude.toDouble()
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
            MarkerOptions().position(office).title("Sede ${officesList.Items[index].city}")
                        .snippet("${officesList.Items[index].name}").draggable(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon)))
            }
    }

}