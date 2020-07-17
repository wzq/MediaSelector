package com.wzq.media.selector.basic

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.config.SelectorType
import com.wzq.media.selector.core.model.MediaData
import kotlinx.android.synthetic.main.activity_selector.*

/**
 * create by wzq on 2020/7/16
 *
 */
class SelectorActivity : AppCompatActivity() {
    private val adapter = SelectorAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)

        val listView = findViewById<RecyclerView>(R.id.listView)
        listView.layoutManager = GridLayoutManager(this, 3)
        listView.adapter = adapter

        loadImages()
        btn.setOnClickListener {
            val list = it.tag as List<MediaData>
            println(list)
            adapter.submitList(list)
        }
    }

    private fun loadImages() {
        MediaSelector(this, SelectorType.IMAGE).querySource {
            test(it)
            adapter.submitList(it)
        }
    }

    var s = 0
    private fun test(list: List<MediaData>) {
        val map = list.groupBy { et ->
            et.dirName
        }.toList().sortedByDescending { p -> p.second.size }
        if (s < map.size -1) btn.tag = map[s++].second
    }
}