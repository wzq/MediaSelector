package com.wzq.media.selector.basic.preview

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.wzq.media.selector.basic.R
import com.wzq.media.selector.core.config.SelectorType
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/7/21
 *
 */
class PreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val pager = findViewById<ViewPager>(R.id.pager)
        val items = intent?.getParcelableArrayListExtra<MediaData>("data") ?: return
        val position = intent?.getIntExtra("position", 0) ?: 0
        supportActionBar?.title = items[position].name
        val type = intent.getSerializableExtra("type") as? SelectorType ?: return
        pager.adapter =
            PreviewAdapter(
                type,
                supportFragmentManager,
                items
            )
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                supportActionBar?.title = items[p0].name
            }

        })
        pager.currentItem = position
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    class PreviewAdapter(
        private val type: SelectorType,
        fragmentManager: FragmentManager,
        private val data: List<MediaData>
    ) :
        FragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(p0: Int): Fragment {
            return if (type == SelectorType.VIDEO)
                VideoFragment.newInstance(data[p0])
            else
                ImageFragment.newInstance(data[p0])
        }

        override fun getCount(): Int {
            return data.size
        }

    }
}