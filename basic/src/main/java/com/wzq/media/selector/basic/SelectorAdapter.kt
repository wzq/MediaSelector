package com.wzq.media.selector.basic

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wzq.media.selector.basic.preview.PreviewActivity
import com.wzq.media.selector.core.model.MediaData


/**
 * create by wzq on 2020/7/16
 *
 */
class SelectorAdapter :
    ListAdapter<MediaData, SelectorAdapter.Holder>(Diff()) {

    val selectedList = arrayListOf<MediaData>()

    var limit = 0

    var onItemClick: ((Int) -> Unit)? = null

    var canPreview = false

    private val onItemStateChange = fun(itemData: MediaData): Boolean {
        if (limit > 0 && selectedList.size >= limit){
            return false
        }
        if (itemData.state) {
            selectedList.remove(itemData)
        } else {
            if (selectedList.contains(itemData)) {
                selectedList.remove(itemData)
            }
            selectedList.add(itemData)
        }
        onItemClick?.invoke(selectedList.size)
        return true
    }

    private val onItemPreview = fun(context: Context, position: Int) {
        if (canPreview) {
            val intent = Intent(context, PreviewActivity::class.java)
            val data = arrayListOf<MediaData>()
            data.addAll(currentList)
            intent.putParcelableArrayListExtra("data", data)
            intent.putExtra("position", position)
            context.startActivity(intent)
        }
    }

    class Diff : DiffUtil.ItemCallback<MediaData>() {
        override fun areItemsTheSame(oldItem: MediaData, newItem: MediaData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MediaData, newItem: MediaData): Boolean {
            return oldItem.name == newItem.name
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        val root = LayoutInflater.from(p0.context).inflate(R.layout.item_selector, p0, false)
        return Holder(root, onItemPreview, onItemStateChange)
    }

    override fun onBindViewHolder(holder: Holder, p1: Int) {
        val item = getItem(p1)
        Glide.with(holder.img).load(item.uri).apply(RequestOptions().skipMemoryCache(true))
            .into(holder.img)
        holder.checkBox.isSelected = item.state
        holder.checkBox.tag = item
    }

    class Holder(
        root: View,
        onPreview: (Context, Int) -> Unit,
        onStateChange: (MediaData) -> Boolean
    ) :
        RecyclerView.ViewHolder(root) {
        val img: ImageView = root.findViewById(R.id.img)
        val checkBox: View = root.findViewById(R.id.checkbox)

        init {
            checkBox.setOnClickListener {
                val itemData = (it.tag as? MediaData) ?: return@setOnClickListener
                if (onStateChange(itemData)) {
                    itemData.state = !itemData.state
                    checkBox.isSelected = itemData.state
                }
            }
            img.setOnClickListener {
                onPreview(it.context, bindingAdapterPosition)
            }
        }
    }
}