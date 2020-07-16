package com.wzq.media.selector.core.model

import android.net.Uri

data class MediaData(
    val uri: Uri,
    val name: String,
    val size: Int,
    val path: String,
    val dirId: String,
    val dirName: String,

    val duration: Int = 0
)