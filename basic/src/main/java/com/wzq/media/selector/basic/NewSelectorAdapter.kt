package com.wzq.media.selector.basic

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wzq.media.selector.core.model.MediaData
import com.wzq.media.selector.core.model.MediaInfo
import java.text.DecimalFormat


/**
 * create by wzq on 2020/7/16
 *
 */
class NewSelectorAdapter :
    ListAdapter<MediaInfo, NewSelectorAdapter.Holder>(Diff()) {
    class Diff : DiffUtil.ItemCallback<MediaInfo>() {
        override fun areItemsTheSame(oldItem: MediaInfo, newItem: MediaInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MediaInfo, newItem: MediaInfo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        val root = LayoutInflater.from(p0.context).inflate(R.layout.item_selector, p0, false)
        return Holder(root)
    }

    override fun onBindViewHolder(p0: Holder, p1: Int) {
        val item = getItem(p1)
        Glide.with(p0.img).load(item.uri).apply(RequestOptions().skipMemoryCache(true)).into(p0.img)
//        p0.checkbox.isSelected = item.state
//        p0.duration.visibility = if (item.duration < 0) View.GONE else {
//            p0.duration.text = timeFormat(item.duration)
//            View.VISIBLE
//        }
        p0.itemView.tag = item
    }

    private fun timeFormat(time: Long): String {
        val s = time / 1000
        val ss = s % 60
        val m = s / 60
        val mm = m % 60
        val h = m / 60

        val df = DecimalFormat("00")
        val str = StringBuilder()
        if (h > 0) {
            str.append(df.format(h)).append(":")
        }
        str.append(df.format(mm)).append(":").append(df.format(ss))
        return str.toString()
    }

    inner class Holder(root: View) : RecyclerView.ViewHolder(root) {
        val img: ImageView = root.findViewById(R.id.img)
    }


}