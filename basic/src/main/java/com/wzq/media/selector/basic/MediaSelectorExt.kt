package com.wzq.media.selector.basic

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/7/17
 *
 */
fun MediaSelector.openBasicPage(activity: Activity, reqCode: Int = MediaSelector.SELECTOR_REQ) {
    querySource { list ->
        if (list.isNullOrEmpty()) {
            Toast.makeText(activity, R.string.basic_data_empty, Toast.LENGTH_LONG).show()
        } else {
            val data =  arrayListOf<MediaData>()
            data.addAll(list)
            val intent = Intent(activity, SelectorActivity::class.java)
            intent.putParcelableArrayListExtra("data", data)
            intent.putExtra("config", mConfig)
            activity.startActivityForResult(intent, MediaSelector.SELECTOR_REQ)
        }
    }
}