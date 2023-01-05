package com.polware.sophosmobileapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polware.sophosmobileapp.data.models.DocumentItems
import com.polware.sophosmobileapp.databinding.ItemDocumentBinding

class AdapterViewDocument(private val documentListener: (image: String) -> Unit):
    RecyclerView.Adapter<AdapterViewDocument.ViewHolder>() {

    private var documentItem = ArrayList<DocumentItems>()

    fun setDocumentList(documentList : List<DocumentItems>){
        this.documentItem = documentList as ArrayList<DocumentItems>
        notifyDataSetChanged()
    }

    // Binding del layout "item_document"
    class ViewHolder(bindingAdapter: ItemDocumentBinding):
        RecyclerView.ViewHolder(bindingAdapter.root) {
        private val documentDate = bindingAdapter.textViewDate
        private val documentType = bindingAdapter.textViewDocument
        private val completeName = bindingAdapter.textViewCompleteName

        fun bind(document: DocumentItems, documentListener: (image: String) -> Unit) {
            val dateFormat = (document.date).split("T").toTypedArray()
            documentDate.text = dateFormat[0]
            //documentType.text = document.fileType
            documentType.text = document.attachedFile
            val stringBuilder = StringBuilder().append("${document.name} ")
                .append(document.lastName).toString()
            completeName.text = stringBuilder
            val documentImage = document.fileType
            itemView.setOnClickListener {
                documentListener.invoke(documentImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDocumentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(documentItem[position], documentListener)
    }

    override fun getItemCount(): Int {
        return documentItem.size
    }

}