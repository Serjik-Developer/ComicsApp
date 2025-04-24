package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.model.comment.response.CommentResponseModel
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemCommentClickListener

class CommentsAdapter(private var comments: MutableList<CommentResponseModel>, private val preferencesManager: PreferencesManager, private val listener: OnItemCommentClickListener) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentsViewHolder(view)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    fun updateComments(newComments: MutableList<CommentResponseModel>) {
        comments = newComments
    }

    inner class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewAuthor = itemView.findViewById<TextView>(R.id.commentAuthor)
        private val textViewText = itemView.findViewById<TextView>(R.id.commentText)
        private val deleteButton = itemView.findViewById<ImageButton>(R.id.deleteButtonComment)
        fun bind(comment: CommentResponseModel) {
            if (comment.isCommentMy) {
                deleteButton.visibility = View.VISIBLE
                deleteButton.setOnClickListener {
                    listener.onDeleteClick(comment.id)
                }
                textViewAuthor.text = comment.user_name
                textViewText.text = comment.text
                textViewAuthor.setOnClickListener {
                    listener.onUserClick(comment.user_id)
                }
            }
            else {
                deleteButton.visibility = View.GONE
                textViewText.text = comment.text
                textViewAuthor.text = comment.user_name
                textViewAuthor.setOnClickListener {
                    listener.onUserClick(comment.user_id)
                }
            }
        }
    }
}