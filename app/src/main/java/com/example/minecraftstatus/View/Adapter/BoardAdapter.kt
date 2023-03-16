package com.example.minecraftstatus.View.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minecraftstatus.Data.ServerItem
import com.example.minecraftstatus.R

class BoardAdapter(val itemList: ArrayList<ServerItem>) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.server_status.text = itemList[position].status
        holder.server_host.text = itemList[position].host
        holder.server_version.text = itemList[position].version
        holder.server_people.text = itemList[position].people
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }


    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val server_status = itemView.findViewById<TextView>(R.id.server_status)
        val server_host = itemView.findViewById<TextView>(R.id.server_host)
        val server_version = itemView.findViewById<TextView>(R.id.server_version)
        val server_people = itemView.findViewById<TextView>(R.id.server_people)
    }
}