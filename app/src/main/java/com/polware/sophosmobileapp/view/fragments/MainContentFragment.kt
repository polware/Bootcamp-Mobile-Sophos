package com.polware.sophosmobileapp.view.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.data.Constants
import com.polware.sophosmobileapp.databinding.FragmentMainContentBinding
import com.polware.sophosmobileapp.view.activities.MainActivity.Companion.currentLanguage
import com.polware.sophosmobileapp.view.activities.SendDocumentActivity
import com.polware.sophosmobileapp.view.activities.SignInActivity
import java.util.*

open class MainContentFragment : Fragment() {
    private var _bindingMC: FragmentMainContentBinding? = null
    private val bindingMainContent get() = _bindingMC!!
    private lateinit var navController: NavController
    private lateinit var mySharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _bindingMC = FragmentMainContentBinding.inflate(inflater, container, false)
        return bindingMainContent.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        mySharedPreferences = requireActivity().getSharedPreferences(Constants.LOGIN_PREFERENCES, Context.MODE_PRIVATE)
        val userName = mySharedPreferences.getString("Username", "").toString()

        setCorporateImage()
        (activity as AppCompatActivity?)!!.setSupportActionBar(bindingMainContent.toolbarMain)
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.title = userName
        setupOptionsMenu()

        bindingMainContent.buttonSend.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainContentFragment_to_sendDocumentActivity)
        }

        bindingMainContent.buttonViewDoc.setOnClickListener {
            val action = MainContentFragmentDirections.actionMainContentFragmentToViewDocumentsFragment()
            navController.navigate(action)
        }

        bindingMainContent.buttonOffices.setOnClickListener {
            val action = MainContentFragmentDirections.actionMainContentFragmentToOfficesMapFragment()
            navController.navigate(action)
        }

    }

    private fun setupOptionsMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId){
                    R.id.action_send_document -> {
                        startActivity(Intent(requireActivity(), SendDocumentActivity::class.java))
                        true
                    }
                    R.id.action_view_document -> {
                        navController.navigate(R.id.action_mainContentFragment_to_viewDocumentsFragment)
                        true
                    }
                    R.id.action_office_map -> {
                        navController.navigate(R.id.action_mainContentFragment_to_officesMapFragment)
                        true
                    }
                    R.id.action_mode_theme -> {
                        changeAppTheme()
                        true
                    }
                    R.id.action_language -> {
                        changeLanguage(requireActivity(), currentLanguage)
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

    private fun setCorporateImage() {
        // Change Corporate Image
        if (currentLanguage == "es") {
            bindingMainContent.imageViewMain.setImageResource(R.drawable.welcome_sophos)
        }
        else {
            bindingMainContent.imageViewMain.setImageResource(R.drawable.english_welcome_sophos)
        }
    }

    fun changeAppTheme() {
        mySharedPreferences = requireActivity().getSharedPreferences(Constants.THEME_PREFERENCES, Context.MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        val themeState = mySharedPreferences.getString(Constants.CURRENT_THEME, "")
        if (themeState.equals("dark_mode")) {
            // If dark mode is ON, it will turn off
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putString(Constants.CURRENT_THEME, "light_mode")
            editor.apply()
        }
        else {
            // If dark mode is OFF, it will turn on
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putString(Constants.CURRENT_THEME, "dark_mode")
            editor.apply()
        }
        // Refresh the fragment
        val ft = parentFragmentManager.beginTransaction()
        ft.detach(this).attach(this).commit()
    }

    fun changeLanguage(activity: Activity, languageCode: String) {
        val newLanguage = if (languageCode == "en" || languageCode == "en_US") {
            "es"
        } else {
            "en"
        }
        val locale = Locale(newLanguage)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        val refresh = Intent(requireContext(), activity::class.java)
        refresh.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(refresh)
    }

    fun signOut() {
        val intent = Intent(activity, SignInActivity::class.java)
        activity?.startActivity(intent)
        activity?.finish()
    }

}