package com.polware.sophosmobileapp.view.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.polware.sophosmobileapp.R
import com.polware.sophosmobileapp.databinding.FragmentViewDocumentsBinding
import com.polware.sophosmobileapp.view.activities.MainActivity.Companion.currentLanguage
import com.polware.sophosmobileapp.view.activities.SendDocumentActivity
import com.polware.sophosmobileapp.view.adapters.AdapterViewDocument
import com.polware.sophosmobileapp.viewmodels.DocumentViewModel
import java.lang.IllegalArgumentException

class ViewDocumentsFragment : MainContentFragment() {
    private var _bindingVD: FragmentViewDocumentsBinding? = null
    private val bindingViewDocuments get() = _bindingVD!!
    private lateinit var viewModel: DocumentViewModel
    private lateinit var adapterDocument : AdapterViewDocument
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _bindingVD = FragmentViewDocumentsBinding.inflate(inflater, container, false)
        return bindingViewDocuments.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        (activity as AppCompatActivity?)!!.setSupportActionBar(bindingViewDocuments.toolbarViewDocs)
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = resources.getString(R.string.toolbar_title)
        setupFragmentMenu()

        setupRecyclerView()
        viewModel = ViewModelProvider(this)[DocumentViewModel::class.java]
        viewModel.getDocumentList()
        viewModel.observeDocumentLiveData().observe(viewLifecycleOwner) { documents ->
            adapterDocument.setDocumentList(documents)
        }

        bindingViewDocuments.toolbarViewDocs.setNavigationOnClickListener {
            //navController.navigateUp()
            navController.navigate(R.id.mainContentFragment)
        }

    }

    private fun setupFragmentMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menu.clear()
                menuInflater.inflate(R.menu.menu_view_doc, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId){
                    R.id.action_nav_view_senddoc -> {
                        val intent = Intent(activity, SendDocumentActivity::class.java)
                        activity?.startActivity(intent)
                        true
                    }
                    R.id.action_nav_view_offices -> {
                        navController.navigate(R.id.action_viewDocumentsFragment_to_officesMapFragment)
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

    private fun setupRecyclerView() {
        adapterDocument = AdapterViewDocument { showImage -> decodeImage(showImage) }
        bindingViewDocuments.recyclerViewListDocuments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterDocument
            bindingViewDocuments.recyclerViewListDocuments.visibility = View.VISIBLE
            bindingViewDocuments.textViewNoRecords.visibility = View.GONE
        }
    }

    // Decode base64 string to image
    private fun decodeImage(imageString: String) {
        Log.i("DocumentListener", imageString)
        try {
            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bindingViewDocuments.imageViewDocImage.setImageBitmap(decodedImage)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(context, "Invalid Base-64 format", Toast.LENGTH_SHORT).show()
            Log.e("Base64Decode", e.message.toString())
        }
    }

}