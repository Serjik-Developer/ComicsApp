package com.example.texnostrelka_2025_otbor.data.mapper

import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.data.remote.model.image.response.ImageNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetworkModel
import com.example.texnostrelka_2025_otbor.presentation.utils.base64ToBitmap
import com.example.texnostrelka_2025_otbor.presentation.utils.toBase64

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

fun convertLocalToNetworkModel(pageId: String, images: List<ImageModel>): PageFromNetworkModel {
    return PageFromNetworkModel(
        pageId = pageId,
        number = 0,
        rows = calculateRows(images),
        columns = calculateColumns(images),
        images = images.map {
            ImageNetworkModel(
                id = it.id,
                cellIndex = it.cellIndex,
                image = it.image?.toBase64()
            )
        }.toMutableList()
    )
}

private fun calculateRows(images: List<ImageModel>): Int {
    return 1
}

private fun calculateColumns(images: List<ImageModel>): Int {
    return 1
}