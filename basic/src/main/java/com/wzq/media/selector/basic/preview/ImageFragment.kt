package com.wzq.media.selector.basic.preview

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/8/5
 *
 */
class ImageFragment : Fragment() {
    private val isOriginal = false
    
    companion object{
        fun newInstance(data: MediaData): Fragment{
            val args = Bundle()
            args.putParcelable("data", data)
            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val photoView = PhotoView(requireContext())
        photoView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return photoView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = arguments?.getParcelable<MediaData>("data") ?: return
        if (view is PhotoView) {
            if (isOriginal) {
                view.setImageURI(data.uri)
            } else {
                Glide.with(view).load(data.uri).into(view)
            }
        }
    }
}