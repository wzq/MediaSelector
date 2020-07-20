package com.wzq.media.selector.basic

import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.PermissionFragment
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/7/17
 *
 */
fun MediaSelector.openBasicPage(
    activity: FragmentActivity,
    reqCode: Int = MediaSelector.SELECTOR_REQ
) {
    PermissionFragment.request(activity.supportFragmentManager) { hasPermission ->
        if (hasPermission) {
            querySource { list ->
                if (list.isNullOrEmpty()) {
                    Toast.makeText(activity, R.string.basic_data_empty, Toast.LENGTH_LONG).show()
                } else {
                    val data = arrayListOf<MediaData>()
                    data.addAll(list)
                    val intent = Intent(activity, SelectorActivity::class.java)
                    intent.putParcelableArrayListExtra("data", data)
                    intent.putExtra("config", mConfig)
                    activity.startActivityForResult(intent, MediaSelector.SELECTOR_REQ)
                }
            }
        } else {
            Toast.makeText(activity, R.string.basic_permission, Toast.LENGTH_LONG).show()
        }
    }
}