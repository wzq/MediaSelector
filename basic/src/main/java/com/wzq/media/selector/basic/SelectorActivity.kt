package com.wzq.media.selector.basic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.TextView
import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/7/16
 * basic MediaSelector
 */
class SelectorActivity : AppCompatActivity() {

    private val data = mutableListOf<Pair<String, List<MediaData>>>()

    private val titleView by lazy {
        findViewById<TextView>(R.id.toolbar_title)
    }
    private val adapter by lazy {
        val config = intent.getParcelableExtra<SelectorConfig>("config")
        val limit = config?.limit ?: 1
        SelectorAdapter(limit)
    }

    private val popupMenu by lazy {
        PopupMenu(this, titleView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleView.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                popupMenu.show()
            } else {
                popupMenu.dismiss()
            }
        }

        val listView = findViewById<RecyclerView>(R.id.listView)
        listView.layoutManager = GridLayoutManager(this, 3)
        listView.adapter = adapter
        loadImages()
    }

    private fun selectColumn(index: Int) {
        if (index > -1 && index < data.size) {
            val item = data[index]
            titleView.text = item.first
            titleView.isSelected = false
            adapter.submitList(item.second)
        }
    }

    private fun loadImages() {
        val list = intent.getParcelableArrayListExtra<MediaData>("data") ?: return
        if (list.isEmpty()) return
        data.clear()
        data.add(Pair("全部", list))
        val columns = list.groupBy { et ->
            et.dirName
        }.toList().sortedByDescending { p -> p.second.size }
        data.addAll(columns)
        buildMenus()
        selectColumn(0)//default index
    }

    private fun buildMenus() {
        data.forEachIndexed { index, pair ->
            val str = "${pair.first}  (${pair.second.size})"
            popupMenu.menu.add(0, index, index, str)
        }
        popupMenu.setOnMenuItemClickListener {
            selectColumn(it.itemId)
            true
        }
        popupMenu.setOnDismissListener { titleView.isSelected = false }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            submitSelect()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun submitSelect() {
        val data = Intent().putParcelableArrayListExtra("data", adapter.selectedItems)
        setResult(Activity.RESULT_OK, data)
    }
}