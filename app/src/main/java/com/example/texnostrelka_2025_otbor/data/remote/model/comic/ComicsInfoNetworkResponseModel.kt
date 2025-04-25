package com.example.texnostrelka_2025_otbor.data.remote.model.comic

import com.example.texnostrelka_2025_otbor.data.remote.model.comment.request.CommentRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comment.response.CommentResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetworkModel

data class ComicsInfoNetworkResponseModel (
    val id: String,
    val text: String? = null,
    val description:  String? = null,
    val creator: String? = null,
    val creator_name: String? = null,
    val firstPage : PageFromNetworkModel,
    val likesCount : Int = 0,
    val userLiked: Boolean,
    val userFavorited: Boolean,
    val comments: MutableList<CommentResponseModel>
)