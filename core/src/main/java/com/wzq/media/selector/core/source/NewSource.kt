package com.wzq.media.selector.core.source

import android.content.Context
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.wzq.media.selector.core.config.MimeType
import com.wzq.media.selector.core.model.MediaInfo

/**
 * create by wzq on 2021/5/20
 *
 */
class NewSource {

    class Rules {
        val projection: Array<String> = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.ORIENTATION,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
        )
        val sortOrder: String = MediaStore.Images.Media.DATE_ADDED.plus(" DESC")
        var selection: String? = null
        var selectionArgs: Array<String>? = null

        fun setMimeType(list: List<MimeType>) {
            if (list.isEmpty()) return
            selection = StringBuilder(MediaStore.Images.Media.MIME_TYPE)
                .append(" in (").also {
                    list.forEachIndexed { index, _ ->
                        if (index > 0) {
                            it.append(",")
                        }
                        it.append("?")
                    }
                }.append(")").toString()
            selectionArgs = list.map { it.value }.toTypedArray()
        }
    }

    @WorkerThread
    fun getMediaSource(context: Context): List<MediaInfo> {
        return readMedia(context, Rules())
    }

    private fun readMedia(context: Context, rules: Rules): List<MediaInfo> {
        val contentResolver = context.contentResolver
        val mediaList = mutableListOf<MediaInfo>()
        try {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                rules.projection,
                rules.selection,
                rules.selectionArgs,
                rules.sortOrder
            )?.use { cursor ->

                //cache column index
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val orientationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION)
                val createTimeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

                val dirIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val dirNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getString(idColumn)
                    if (id.isNullOrEmpty()) continue
                    val name = cursor.getString(nameColumn) ?: "unknown"
                    val size = cursor.getLong(sizeColumn)
                    val orientation = cursor.getInt(orientationColumn)
                    val createTime = cursor.getString(createTimeColumn)
                    val dirId = cursor.getString(dirIdColumn)
                    val dirName = cursor.getString(dirNameColumn)
                    val data = cursor.getString(dataColumn)

                    mediaList += MediaInfo(
                        id,
                        name,
                        size,
                        orientation,
                        createTime,
                        dirId,
                        dirName,
                        data
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mediaList
    }
}