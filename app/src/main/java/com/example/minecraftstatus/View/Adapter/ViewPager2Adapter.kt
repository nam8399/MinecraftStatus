package com.example.minecraftstatus.View.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.recyclerview.widget.RecyclerView
import com.example.minecraftstatus.R

class ViewPager2Adater(var list : ArrayList<Int>,var context : Context)  : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)
        return viewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        (holder as viewHolder).image.setBackgroundColor(list.get(position))
        if(position == 0) {
            (holder as viewHolder).image.setImageResource(R.drawable.banner_1)
            (holder as viewHolder).image.scaleType = ScaleType.CENTER_CROP
        } else if (position == 1) {
            (holder as viewHolder).image.setImageResource(R.drawable.banner_2)
            (holder as viewHolder).image.scaleType = ScaleType.CENTER_CROP
        } else if (position ==2) {
            (holder as viewHolder).image.setImageResource(R.drawable.banner_3)
            (holder as viewHolder).image.scaleType = ScaleType.CENTER_CROP
        }
    }

    inner class viewHolder(var view : View) : RecyclerView.ViewHolder(view){
        var image : ImageView = view.findViewById(R.id.image)
    }
}
