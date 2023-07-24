package com.wzq.mediaselector.sample.data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore

/**
 * create by wzq on 2020/7/15
 *
 */
class ImageSource(private val contentResolver: ContentResolver) {

    enum class MimeType(val value: String) {
        JPEG("image/jpeg"),
        PNG("image/png"),
        GIF("image/gif")
    }

    private val imageList = mutableListOf<PhotoItemData>()

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

    private var selection: String? = null
    private var selectionArgs: Array<String>? = null
    fun setMimeType(list: List<MimeType>) {
        if (list.isEmpty()) return
        selection = StringBuilder(MediaStore.Images.Media.MIME_TYPE).append(" in (").also {
            list.forEachIndexed { index, _ ->
                if (index > 0) {
                    it.append(",")
                }
                it.append("?")
            }
        }.append(")").toString()
        selectionArgs = list.map { it.value }.toTypedArray()
    }

    fun query(callback: (List<PhotoItemData>) -> Unit) {
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            val dirIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val dirNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getInt(idColumn)
                val name = cursor.getString(nameColumn)
                val data = cursor.getString(dataColumn)
                val size = cursor.getLong(sizeColumn)
                val dirId = cursor.getString(dirIdColumn)
                val dirName = cursor.getString(dirNameColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong()
                )
                // Stores column values and the contentUri in a local object
                // that represents the media file.
                imageList += PhotoItemData(
                    id, name, size, contentUri, data,
                )
            }
            callback(imageList)
        }
    }
}