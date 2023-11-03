package com.example.superwolffirebase.adapter

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ItemPagerBinding
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Objects

class ViewPagerAdapter(
    private val imageList: List<Int>
) : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(val binding: ItemPagerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
        return Pager2ViewHolder(
            ItemPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        holder.binding.image.setImageResource(imageList[position])
    }


}