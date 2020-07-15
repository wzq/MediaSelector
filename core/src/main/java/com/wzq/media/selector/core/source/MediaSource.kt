package com.wzq.media.selector.core.source

import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/7/15
 *
 */
interface MediaSource {

    fun setConfig(config: SelectorConfig)

    fun query(callback: (List<MediaData>) -> Unit): Unit?
}