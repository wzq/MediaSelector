package com.wzq.media.selector.core.config

import android.os.Parcel
import android.os.Parcelable

/**
 * create by wzq on 2020/7/15
 *
 */
data class SelectorConfig(
    val limit: Int = 1
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(limit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectorConfig> {
        override fun createFromParcel(parcel: Parcel): SelectorConfig {
            return SelectorConfig(parcel)
        }

        override fun newArray(size: Int): Array<SelectorConfig?> {
            return arrayOfNulls(size)
        }
    }
}