package com.example.texnostrelka_2025_otbor.presentation.listener

interface OnItemEditPageNetworkClickListener {
    fun onDeleteClick(id: String)
    fun onEditClick(id: String)
    fun onAddClick(pageId: String, cellIndex: Int)
}