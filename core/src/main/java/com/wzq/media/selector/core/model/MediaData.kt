package com.wzq.media.selector.core.model

import android.content.ContentResolver
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size

data class MediaData(
    val uri: Uri,
    val name: String,
    val size: Int,
    val path: String,
    val dirId: String,
    val dirName: String,

    val duration: Int = 0
) {
    fun getThumb(contentResolver: ContentResolver): Bitmap? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ThumbnailUtils.createImageThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND)
        } else {
            contentResolver.loadThumbnail(uri, Size(512, 348), null)
        }
    }
}