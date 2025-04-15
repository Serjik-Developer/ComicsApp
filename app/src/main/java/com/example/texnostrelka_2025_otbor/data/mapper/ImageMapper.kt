package com.example.texnostrelka_2025_otbor.data.mapper

import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.data.remote.model.image.response.ImageNetworkModel
import com.example.texnostrelka_2025_otbor.presentation.utils.base64ToBitmap

fun convertNetworkToModel(imageList: MutableList<ImageNetworkModel>?, pageId: String) : List<ImageModel>{
    return imageList?.map { networkItem ->
        ImageModel(
            id = networkItem.id,
            pageId = pageId,
            image = networkItem.image?.base64ToBitmap(),
            cellIndex = networkItem.cellIndex
        )
    } ?: emptyList()
}