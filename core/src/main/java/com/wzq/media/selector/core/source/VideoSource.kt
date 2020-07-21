package com.wzq.media.selector.core.source

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.wzq.media.selector.core.config.MimeType
import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.model.MediaData
import java.util.concurrent.TimeUnit

/**
 * create by wzq on 2020/7/15
 *
 */
class VideoSource(contentResolver: ContentResolver) : MediaSource {

    private val videoList = mutableListOf<MediaData>()

    private val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.BUCKET_ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME
    )

    // Display videos in alphabetical order based on their display name.
    private val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

    private val query by lazy {
        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
    }

    private var selection: String? = null
    private var selectionArgs: Array<String>? = null
    override fun setMimeType(list: List<MimeType>) {
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

    override fun query(callback: (List<MediaData>) -> Unit) = query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durationColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
        val dirIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
        val dirNameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            // Get values of columns for a given video.
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val duration = cursor.getLong(durationColumn)
            val size = cursor.getInt(sizeColumn)
            val dirId = cursor.getString(dirIdColumn)
            val dirName = cursor.getString(dirNameColumn)
            val data = cursor.getString(dataColumn)

            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                id
            )
            // Stores column values and the contentUri in a local object
            // that represents the media file.
            videoList += MediaData(
                contentUri,
                name,
                size,
                data,
                dirId,
                dirName,
                duration
            )
        }
        callback(videoList)
    }
}