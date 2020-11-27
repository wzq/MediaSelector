package com.wzq.media.selector.core.source

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.wzq.media.selector.core.config.MimeType
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/7/15
 *
 */
class ImageSource(private val contentResolver: ContentResolver) : MediaSource {

    private val imageList = mutableListOf<MediaData>()

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    // Display videos in alphabetical order based on their display name.
    private val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

    var selection: String? = null
    var selectionArgs: Array<String>? = null
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

    override fun query(callback: (List<MediaData>) -> Unit) {
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            val dirIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val dirNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val data = cursor.getString(dataColumn)
                val size = cursor.getInt(sizeColumn)
                val dirId = cursor.getString(dirIdColumn)
                val dirName = cursor.getString(dirNameColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                // Stores column values and the contentUri in a local object
                // that represents the media file.
                imageList += MediaData(
                    contentUri,
                    name,
                    size,
                    data,
                    dirId,
                    dirName
                )
            }
            callback(imageList)
        }
    }
}