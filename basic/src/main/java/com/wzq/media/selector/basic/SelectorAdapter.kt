package com.wzq.media.selector.basic

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.wzq.media.selector.core.model.MediaData


/**
 * create by wzq on 2020/7/16
 *
 */
class SelectorAdapter(val limit: Int) :
    ListAdapter<MediaData, SelectorAdapter.Holder>(Diff()) {

    val selectedItems = arrayListOf<MediaData>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        val root = LayoutInflater.from(p0.context).inflate(R.layout.item_selector, p0, false)
        return Holder(root)
    }

    override fun onBindViewHolder(p0: Holder, p1: Int) {
        val item = getItem(p1)
        Glide.with(p0.img).load(item.uri).skipMemoryCache(true).into(p0.img)
        p0.checkbox.isSelected = item.state
        p0.itemView.tag = item
    }

    inner class Holder(root: View) : RecyclerView.ViewHolder(root) {
        val img: ImageView = root.findViewById(R.id.img)
        val checkbox: ImageView = root.findViewById(R.id.checkbox)
        private val tip = root.context.getString(R.string.basic_limit_tips, limit)

        init {
            itemView.setOnClickListener {
                val item = it.tag as? MediaData ?: return@setOnClickListener
                val state = !checkbox.isSelected
                if (state) {
                    if (selectedItems.size < limit) {
                        checkbox.isSelected = state
                        item.state = state
                        selectedItems.add(item)
                    } else {
                        Toast.makeText(it.context, tip, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    checkbox.isSelected = state
                    selectedItems.remove(item)
                    item.state = state
                }
            }
        }
    }

    class Diff : DiffUtil.ItemCallback<MediaData>() {
        override fun areItemsTheSame(p0: MediaData, p1: MediaData): Boolean {
            return p0.uri == p1.uri
        }

        override fun areContentsTheSame(p0: MediaData, p1: MediaData): Boolean {
            return p0.name == p1.name
        }

    }


}