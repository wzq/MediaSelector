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
class VideoSource(contentResolver: ContentResolver): MediaSource {

    data class Video(
        val uri: Uri,
        val name: String,
        val duration: Int,
        val size: Int
    )

    val videoList = mutableListOf<Video>()

    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE
    )

    // Show only videos that are at least 5 minutes in duration.
    val selection = "${MediaStore.Video.Media.DURATION} >= ?"
    val selectionArgs = arrayOf(
        TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
    )

    // Display videos in alphabetical order based on their display name.
    val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} Desc"

    val query = contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )

    override fun setMimeType(list: List<MimeType>) {
    }

    override fun query(callback: (List<MediaData>) -> Unit) = query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durationColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

        while (cursor.moveToNext()) {
            // Get values of columns for a given video.
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val duration = cursor.getInt(durationColumn)
            val size = cursor.getInt(sizeColumn)

            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                id
            )
            // Stores column values and the contentUri in a local object
            // that represents the media file.
            videoList += Video(
                contentUri,
                name,
                duration,
                size
            )
        }
    }
}