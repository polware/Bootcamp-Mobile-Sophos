package com.polware.sophosmobileapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polware.sophosmobileapp.data.models.DocumentItems
import com.polware.sophosmobileapp.databinding.ItemDocumentBinding

class AdapterViewDocument(private val documents: List<DocumentItems>,
                          private val documentListener: (image: String) -> Unit):
    RecyclerView.Adapter<AdapterViewDocument.ViewHolder>() {

    // Binding del layout "item_document"
    class ViewHolder(bindingAdapter: ItemDocumentBinding):
        RecyclerView.ViewHolder(bindingAdapter.root) {
        val documentDetails = bindingAdapter.linearLayoutDocDetails
        val documentDate = bindingAdapter.textViewDate
        val documentType = bindingAdapter.textViewDocument
        val completeName = bindingAdapter.textViewCompleteName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDocumentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val documentImage = documents[position].attachedFile
        val documentImage = documents[position].fileType

        val dateFormat = (documents[position].date).split("T").toTypedArray()
        holder.documentDate.text = dateFormat[0]
        holder.documentType.text = documents[position].docType
        val stringBuilder = StringBuilder().append("${documents[position].name} ")
            .append(documents[position].lastName).toString()
        holder.completeName.text = stringBuilder

        holder.documentDetails.setOnClickListener {
            documentListener.invoke(documentImage)
        }
    }

    override fun getItemCount(): Int {
        return documents.size
    }

}