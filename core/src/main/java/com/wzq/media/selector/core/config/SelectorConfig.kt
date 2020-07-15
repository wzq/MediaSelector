package com.wzq.media.selector.core.config

/**
 * create by wzq on 2020/7/15
 *
 */
data class SelectorConfig(
    val limit: Int = 1,
    val mime: List<MimeType>? = null
)