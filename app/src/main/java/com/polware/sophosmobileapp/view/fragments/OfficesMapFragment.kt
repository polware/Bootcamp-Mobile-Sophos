package com.polware.sophosmobileapp.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.databinding.FragmentOfficesMapBinding
import com.polware.sophosmobileapp.view.activities.MainActivity
import com.polware.sophosmobileapp.view.activities.SendDocumentActivity
import com.polware.sophosmobileapp.viewmodels.OfficesMapViewModel

class OfficesMapFragment : MainContentFragment(), OnMapReadyCallback {
    private var _bindingOM: FragmentOfficesMapBinding? = null
    private val bindingOfficesMap get() = _bindingOM!!
    private lateinit var navController: NavController
    private val USER_LOCATION_REQUEST_CODE = 100
    private lateinit var viewModel: OfficesMapViewModel
    private lateinit var mMap: GoogleMap
    private var mapView: MapView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _bindingOM = FragmentOfficesMapBinding.inflate(inflater, container, false)
        return bindingOfficesMap.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        (activity as AppCompatActivity?)!!.setSupportActionBar(bindingOfficesMap.toolbarMap)
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = resources.getString(R.string.toolbar_title)
        setupFragmentMenu()

        bindingOfficesMap.toolbarMap.setNavigationOnClickListener {
            navController.navigate(R.id.action_officesMapFragment_to_mainContentFragment)
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        }
        else {
            viewModel = ViewModelProvider(this)[OfficesMapViewModel::class.java]
            viewModel.getOfficesList()
            mapView = bindingOfficesMap.map
            if (mapView != null) {
                mapView!!.onCreate(null)
                mapView!!.onResume()
                mapView!!.getMapAsync(this)
            }
        }

    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                USER_LOCATION_REQUEST_CODE)
            return
        }
    }

    private fun setupFragmentMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_offices, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId){
                    R.id.action_nav_off_senddoc -> {
                        startActivity(Intent(requireActivity(), SendDocumentActivity::class.java))
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        true
                    }
                    R.id.action_nav_off_viewdoc -> {
                        navController.navigate(R.id.action_officesMapFragment_to_viewDocumentsFragment)
                        true
                    }
                    R.id.action_mode_theme -> {
                        changeAppTheme()
                        true
                    }
                    R.id.action_language -> {
                        changeLanguage(requireActivity(), MainActivity.currentLanguage)
                        true
                    }
                    R.id.action_sign_out -> {
                        signOut()
                        true
                    }
                    else -> false
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == USER_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //MapFragment()
            }
            else {
                Toast.makeText(requireContext(),
                    "By rejecting the location permission you cannot see the map of the offices",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap = gMap
            gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.isCompassEnabled = true
            mMap.uiSettings.isIndoorLevelPickerEnabled = true
            showOffices()
            Handler(Looper.getMainLooper()).postDelayed({
                pointToUserLocation()
            }, 1500)
        }
    }

    private fun showOffices() {
        viewModel.observeOfficesList().observe(this) {
            officesList ->
            if (officesList != null) {
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
            }
            else {
                Toast.makeText(requireActivity(),
                    resources.getString(R.string.error_message_officesmap_viewmodel), Toast.LENGTH_SHORT).show()
            }
        }
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
            mMap.addMarker(
                MarkerOptions().position(userLocation)
                .title(resources.getString(R.string.map_title_marker)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12f), 2000, null)
        }
    }

}