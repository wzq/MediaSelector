package com.wzq.media.selector.core.model

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Size

data class MediaData(
    val uri: Uri,
    val name: String?,
    val size: Int = 0,
    val path: String?, //该属性已经废弃，尽量使用uri
    val dirId: String?,
    val dirName: String?,
    val duration: Long = -1,
    var state: Boolean = false
) : Parcelable {

    /**
     * 生成缩略图
     */
    fun getThumb(context: Context): Bitmap? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (path.isNullOrBlank()) return null
            ThumbnailUtils.createImageThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND)
        } else {
            context.contentResolver.loadThumbnail(uri, Size(512, 348), null)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uri, flags)
        parcel.writeString(name)
        parcel.writeInt(size)
        parcel.writeString(path)
        parcel.writeString(dirId)
        parcel.writeString(dirName)
        parcel.writeLong(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaData> {
        override fun createFromParcel(parcel: Parcel): MediaData {
            return MediaData(parcel)
        }

        override fun newArray(size: Int): Array<MediaData?> {
            return arrayOfNulls(size)
        }
    }
}