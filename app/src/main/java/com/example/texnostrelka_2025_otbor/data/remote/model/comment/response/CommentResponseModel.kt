package com.example.texnostrelka_2025_otbor.data.remote.model.comment.response

data class CommentResponseModel (
    val id: String,
    val text: String,
    val created_at: String,
    val user_id: String,
    val user_name: String,
    val isCommentMy: Boolean
)