package com.example.texnostrelka_2025_otbor.presentation.listener

import com.example.texnostrelka_2025_otbor.data.model.PageWithImages

interface OnItemPageClickListener {
    fun onDeleteClick(pageId: String)
    fun onEditClick(Page:PageWithImages)
}