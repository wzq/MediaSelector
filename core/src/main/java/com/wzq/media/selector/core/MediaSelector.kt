package com.wzq.media.selector.core

import android.content.Context
import com.wzq.media.selector.core.config.MimeType
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
class MediaSelector(private val context: Context, private val type: SelectorType) {

    companion object {
        const val SELECTOR_REQ = 0x123
        const val SELECTOR_PERM = 0x223

        @JvmStatic
        fun create(context: Context, type: SelectorType): MediaSelector {
            return MediaSelector(context, type)
        }
    }
    private val mime: MutableList<MimeType> = mutableListOf()

    fun mime(vararg mimeType: MimeType): MediaSelector {
        mime.clear()
        mime.addAll(mimeType)
        return this
    }

    fun querySource(callback: (List<MediaData>) -> Unit) {
        val resolver = context.contentResolver
        val source = when (type) {
            SelectorType.IMAGE -> ImageSource(resolver)
            SelectorType.VIDEO -> VideoSource(resolver)
        }
        source.setMimeType(mime)
        source.query(callback)
    }
}