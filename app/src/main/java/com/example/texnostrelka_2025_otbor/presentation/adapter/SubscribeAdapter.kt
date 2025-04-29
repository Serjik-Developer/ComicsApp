package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.remote.model.subscribe.SubscribeUsersResponseModel
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemSubscribedClickListener
import com.example.texnostrelka_2025_otbor.presentation.utils.base64ToBitmap

class SubscribeAdapter(private var users: MutableList<SubscribeUsersResponseModel>, private val listener: OnItemSubscribedClickListener) : RecyclerView.Adapter<SubscribeAdapter.SubscribeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subscribed_users, parent, false)
        return SubscribeViewHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: SubscribeViewHolder, position: Int) {
        holder.bind(users[position])
    }

    fun updateData(newUsers: MutableList<SubscribeUsersResponseModel>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class SubscribeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView = itemView.findViewById(R.id.imageViewAvatar)
        private val userName: TextView = itemView.findViewById(R.id.textViewUsername)
        private val btnSubscribe: Button = itemView.findViewById(R.id.buttonSubscribe)
        fun bind(user: SubscribeUsersResponseModel) {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClickListener(user.id)
                }
            }
            user.avatar?.let {
                userAvatar.setImageBitmap(it.base64ToBitmap())
            } ?: run {
                userAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
            }
            userName.text = user.name
            user.is_subscribed_by_me?.let {
                if (it){
                    btnSubscribe.visibility = View.VISIBLE
                    btnSubscribe.text = "Отписаться"
                } else {
                    btnSubscribe.visibility = View.VISIBLE
                    btnSubscribe.text = "Подписаться"
                }
            } ?: run {
                btnSubscribe.visibility = View.GONE
            }
            btnSubscribe.setOnClickListener {
                val isSubscribed = !user.is_subscribed_by_me!!
                btnSubscribe.text = if (isSubscribed) "Отписаться" else "Подписаться"
                listener.onButtonSubscribeClick(user.id)
            }
        }
    }
}