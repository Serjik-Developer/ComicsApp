package com.example.texnostrelka_2025_otbor.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.remote.model.subscribe.SubscribeUsersResponseModel
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemSubscribedClickListener

    class SubscribeAdapter(private var users: MutableList<SubscribeUsersResponseModel>, private val listener: OnItemSubscribedClickListener) : RecyclerView.Adapter<SubscribeAdapter.SubscribeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subscribed_users, parent, false)
        return SubscribeViewHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: SubscribeViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    fun updateData(newUsers: MutableList<SubscribeUsersResponseModel>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class SubscribeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}