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
 *
 */
class MediaSelector(private val context: Context, private val type: SelectorType) {

    private val source by lazy {
        val resolver = context.contentResolver
        when (type) {
            SelectorType.IMAGE -> ImageSource(resolver)
            SelectorType.VIDEO -> VideoSource(resolver)
        }
    }

    private var mConfig: SelectorConfig? = null
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