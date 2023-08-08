package com.wzq.mediaselector.sample.ui.page

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.wzq.mediaselector.sample.data.PhotoItemData

/**
 * create by wzq on 2023/8/8
 *
 */
class PhotosViewModel: ViewModel() {

    val photos = mutableStateListOf<PhotoItemData>()

    val default = mockItem(0)
    init {
        println("vm init")
        repeat(30) {
            photos.add(mockItem(it))
        }
    }
    private fun fetchPhotos(){
//        ImageSource(resolver).query {
//            photos.addAll(it)
//        }
    }

    fun mockItem(id: Int): PhotoItemData {
        val url = "https://picsum.photos/seed/${(0..100000).random()}/256/256"
        return PhotoItemData(
            id, id.toString(), 1, Uri.parse(url), url
        )
    }
}