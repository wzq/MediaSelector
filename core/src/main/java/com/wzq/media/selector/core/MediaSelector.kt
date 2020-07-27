package com.wzq.media.selector.core

import android.content.Context
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
class MediaSelector(val context: Context, val type: SelectorType) {

    companion object {
        const val SELECTOR_REQ = 0x123
        const val SELECTOR_PERM = 0x223
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
}