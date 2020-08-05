package com.wzq.media.selector.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.wzq.media.selector.core.config.MimeType
import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.config.SelectorType
import com.wzq.media.selector.core.model.MediaData
import com.wzq.media.selector.core.source.ImageSource
import com.wzq.media.selector.core.source.VideoSource

/**
 * create by wzq on 2020/7/15
 * 入口
 * @param context 用于获取contentResolver
 * @param type 资源类型
 */
class MediaSelector(private val context: Context, val type: SelectorType) {

    companion object {
        const val SELECTOR_REQ = 0x123
        const val SELECTOR_PERM = 0x223

        @JvmStatic
        fun create(context: Context, type: SelectorType): MediaSelector {
            return MediaSelector(context, type)
        }
    }

    private val source by lazy {
        val resolver = context.contentResolver
        when (type) {
            SelectorType.IMAGE -> ImageSource(resolver)
            SelectorType.VIDEO -> VideoSource(resolver)
        }
    }

    var mConfig: SelectorConfig? = null
    private val mime: MutableList<MimeType> = mutableListOf()

    fun config(config: SelectorConfig): MediaSelector {
        mConfig = config
        return this
    }

    fun mime(vararg mimeType: MimeType): MediaSelector {
        mime.addAll(mimeType)
        return this
    }

    fun querySource(callback: (List<MediaData>) -> Unit) {
        source.setMimeType(mime)
        source.query(callback)
    }

    fun openPage(activity: FragmentActivity, javaClass: Class<out Activity>, reqCode: Int = SELECTOR_REQ) {
        PermissionFragment.request(activity.supportFragmentManager) { hasPermission ->
            if (hasPermission) {
                querySource { list ->
                    val data = arrayListOf<MediaData>()
                    data.addAll(list)
                    val intent = Intent(activity, javaClass)
                    intent.putParcelableArrayListExtra("data", data)
                    intent.putExtra("config", mConfig)
                    intent.putExtra("type", type)
                    activity.startActivityForResult(intent, reqCode)
                }
            }
        }
    }
}