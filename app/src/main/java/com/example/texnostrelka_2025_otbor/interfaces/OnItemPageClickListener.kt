package com.example.texnostrelka_2025_otbor.interfaces

import com.example.texnostrelka_2025_otbor.models.PageWithImages

interface OnItemPageClickListener {
    fun onDeleteClick(pageId: String)
    fun onEditClick(Page:PageWithImages)
}