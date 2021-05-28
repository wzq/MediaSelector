package com.wzq.media.selector.core.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore

/**
 * create by wzq on 2021/5/17
 *
 */
data class MediaInfo(
    val id: String,
    val name: String,
    val size: Long,
    val orientation: Int,
    val createTime: String?,
    val dirId: String?, //文件夹ID
    val dirName: String?, //文件夹 名称
    @Deprecated("该属性已经废弃，尽量使用uri")
    val path: String?, //路径
) {

    var uri: Uri? = null
        get() {
            if (field == null){
                val baseUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                field = Uri.withAppendedPath(baseUri, id)
            }
            return field
        }

//    fun getThumbnail(context: Context, uri: Uri = uri): Bitmap? {
//        return try {
//            context.contentResolver.openInputStream(uri)?.use {
//                val bitmap = BitmapFactory.decodeStream(it)
//                ThumbnailUtils.extractThumbnail(bitmap, 640, 320)
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
}
