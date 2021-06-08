package com.wzq.media.selector.basic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wzq.media.selector.core.model.MediaData


/**
 * create by wzq on 2020/7/16
 *
 */
class NewSelectorAdapter :
    ListAdapter<MediaData, NewSelectorAdapter.Holder>(Diff()) {

    val selectedList = arrayListOf<MediaData>()

    private val onItemStateChange = fun(itemData: MediaData) {
        if (itemData.state) {
            selectedList.remove(itemData)
        } else {
            if (selectedList.contains(itemData)) {
                selectedList.remove(itemData)
            }
            selectedList.add(itemData)
        }
    }

    class Diff : DiffUtil.ItemCallback<MediaData>() {
        override fun areItemsTheSame(oldItem: MediaData, newItem: MediaData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MediaData, newItem: MediaData): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        val root = LayoutInflater.from(p0.context).inflate(R.layout.item_selector, p0, false)
        return Holder(root, onItemStateChange)
    }

    override fun onBindViewHolder(holder: Holder, p1: Int) {
        val item = getItem(p1)
        Glide.with(holder.img).load(item.uri).apply(RequestOptions().skipMemoryCache(true))
            .into(holder.img)
        holder.checkBox.isSelected = item.state
        holder.itemView.tag = item
    }

    class Holder(root: View, onStateChange: (MediaData) -> Unit) :
        RecyclerView.ViewHolder(root) {
        val img: ImageView = root.findViewById(R.id.img)
        val checkBox: View = root.findViewById(R.id.checkbox)

        init {
            itemView.setOnClickListener {
                val itemData = (it.tag as? MediaData) ?: return@setOnClickListener
                onStateChange(itemData)
                itemData.state = !itemData.state
                checkBox.isSelected = itemData.state
            }
        }
    }


}