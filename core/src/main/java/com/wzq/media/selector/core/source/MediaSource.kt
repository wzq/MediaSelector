package com.wzq.media.selector.core.source

import com.wzq.media.selector.core.config.MimeType
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/7/15
 *
 */
interface MediaSource {

    fun setMimeType(list: List<MimeType>)

    fun query(callback: (List<MediaData>) -> Unit): Unit?
}