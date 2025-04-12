package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.data.remote.model.ImageNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.PageFromNetwork
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditNetworkAdapter.EditNetworkViewHolder
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.presentation.utils.base64ToBitmap

class EditNetworkAdapter(private var pages: MutableList<PageFromNetwork>, private val context: Context, private val listener: OnItemClickListener): RecyclerView.Adapter<EditNetworkViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditNetworkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pages_item, parent, false)
        return EditNetworkViewHolder(view, context, listener)
    }

    override fun onBindViewHolder(holder: EditNetworkViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData (newPages: MutableList<PageFromNetwork>) {
        pages = newPages
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = pages.size

    inner class EditNetworkViewHolder(val itemView: View, private val context: Context, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        private val imageRecyclerView: RecyclerView = itemView.findViewById(R.id.imageRecyclerView)
        private val editBtn: ImageButton = itemView.findViewById(R.id.edit_page_btn)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_page_btn)

        fun bind(page: PageFromNetwork) {
            val pageId = page.pageId
            val imageNetworkList = page.images
            val imageModelList = convertNetworkToModel(imageNetworkList, pageId)
            imageRecyclerView.layoutManager = GridLayoutManager(context, page.columns)
            imageRecyclerView.adapter = ImageAdapter(imageModelList)
            deleteBtn.setOnClickListener {
                listener.onDeleteClick(page.pageId)
            }
            editBtn.setOnClickListener {
                listener.onEditClick(page.pageId)
            }
        }
        private fun convertNetworkToModel(imageList: MutableList<ImageNetworkModel>?, pageId: String) : List<ImageModel>{
            return imageList?.map { networkItem ->
                ImageModel(
                    id = networkItem.id,
                    pageId = pageId,
                    image = networkItem.image?.base64ToBitmap(),
                    cellIndex = networkItem.cellIndex
                )
            } ?: TODO("CREATE ERROR ABOT PAGE WITHOUT IMAGES")
        }
    }
}