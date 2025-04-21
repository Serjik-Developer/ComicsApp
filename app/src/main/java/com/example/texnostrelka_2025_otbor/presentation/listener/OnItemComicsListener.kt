package com.example.texnostrelka_2025_otbor.presentation.listener

interface OnItemComicsListener {
    fun onItemClick(id: String)
    fun onDeleteClick(id: String)
    fun onEditClick(id: String)
    fun onSendClick(id: String)
    fun onDownloadClick(id: String)
}