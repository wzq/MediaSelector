package com.wzq.mediaselector.sample.data

import android.net.Uri

data class PhotoItemData(
    val id: Int,
    val name: String,
    val size: Long,
    val contentUri: Uri,
    val path: String?
)