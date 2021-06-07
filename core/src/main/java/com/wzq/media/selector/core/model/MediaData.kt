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
    val id: String,
    val name: String, //名称
    val size: Long = 0,  //文件大小
    val orientation: Int,
    val createTime: String?,
    val dirId: String?, //文件夹ID
    val dirName: String?, //文件夹 名称
    @Deprecated("该属性已经废弃，尽量使用uri")
    val path: String?, //路径
    val duration: Long = -1, //视频时长
    var state: Boolean = false //选择状态
) : Parcelable {

    var uri: Uri? = null
        get() {
            if (field == null) {
                val baseUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                field = Uri.withAppendedPath(baseUri, id)
            }
            return field
        }


    /**
     * 生成缩略图
     */
    fun getThumb(context: Context): Bitmap? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (path.isNullOrBlank()) return null
            ThumbnailUtils.createImageThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND)
        } else {
            uri?.let {
                context.contentResolver.loadThumbnail(it, Size(512, 348), null)
            }
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
    ) {
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeLong(size)
        parcel.writeInt(orientation)
        parcel.writeString(createTime)
        parcel.writeString(dirId)
        parcel.writeString(dirName)
        parcel.writeString(path)
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