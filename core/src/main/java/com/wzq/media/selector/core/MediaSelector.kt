package com.wzq.media.selector.core

import android.content.Context
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

    private var mConfig: SelectorConfig? = null

    private var resultCallback: ((List<MediaData>) -> Unit)? = null

    fun config(config: SelectorConfig): MediaSelector {
        mConfig = config
        return this
    }

    fun onResult(callback: (List<MediaData>) -> Unit) {
        resultCallback = callback
    }

    private fun createSource() {
        val resolver = context.contentResolver
        val config = mConfig ?: SelectorConfig() //default config
        val source = when (type) {
            SelectorType.IMAGE -> ImageSource(resolver)
            SelectorType.VIDEO -> VideoSource(resolver)
        }
        resultCallback?.run {
            source.setConfig(config)
            source.query(this)
        }
    }

    operator fun invoke(block: MediaSelector.() -> Unit) {
        block()
        createSource()
    }
}