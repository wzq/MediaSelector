//package com.wzq.media.selector.basic.preview
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.VideoView
//import com.wzq.media.selector.basic.R
//import com.wzq.media.selector.core.model.MediaData
//import kotlinx.android.synthetic.main.fragment_video.*
//import kotlinx.android.synthetic.main.item_selector.view.*
//
///**
// * create by wzq on 2020/8/5
// *
// */
//class VideoFragment : Fragment() {
//
//    companion object {
//        fun newInstance(data: MediaData): Fragment {
//            val args = Bundle()
//            args.putParcelable("data", data)
//            val fragment = VideoFragment()
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    private val data by lazy {
//        arguments?.getParcelable<MediaData>("data")
//    }
//
//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (!isVisibleToUser) {
//            video?.pause()
//            control?.visibility = View.VISIBLE
//        }
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_video, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        video.setVideoURI(data?.uri)
//        video.setOnCompletionListener {
//            video?.resume()
//            control.visibility = View.VISIBLE
//        }
//        view.setOnClickListener {
//            video?.run {
//                if (isPlaying) {
//                    pause()
//                    control.visibility = View.VISIBLE
//                } else {
//                    start()
//                    control.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        video?.stopPlayback()
//        video?.suspend()
//    }
//
//}