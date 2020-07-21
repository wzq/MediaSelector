package com.wzq.media.selector.basic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
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
        SelectorAdapter(limit, updateBottom)
    }

    private val popupMenu by lazy {
        PopupMenu(this, titleView)
    }

    private val ensureBtn by lazy {
        findViewById<Button>(R.id.ensure)
    }
    private val previewBtn by lazy {
        findViewById<Button>(R.id.preview)
    }
    private var limit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        init()
    }

    private fun init() {
        val config = intent.getParcelableExtra<SelectorConfig>("config")
        limit = config?.limit ?: 1
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

        ensureBtn.text = getString(R.string.basic_ensure, 0, limit)
        ensureBtn.setOnClickListener { submitSelect() }
        if (config?.needPreview == true) {
            previewBtn.visibility = View.VISIBLE
            previewBtn.setOnClickListener { preview() }
        } else {
            previewBtn.visibility = View.GONE
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
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun submitSelect() {
        val selected = adapter.selectedItems
        if (selected.isNullOrEmpty()) return
        val data = Intent().putParcelableArrayListExtra("data", adapter.selectedItems)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun preview() {
        val selected = adapter.selectedItems
        if (selected.isNullOrEmpty()) return
        val intent = Intent(this, PreviewActivity::class.java)
            .putParcelableArrayListExtra("data", adapter.selectedItems)
        startActivity(intent)
    }

    private val updateBottom = fun(selected: Int, limit: Int) {
        ensureBtn.isEnabled = selected > 0
        previewBtn.isEnabled = selected > 0
        ensureBtn.text = getString(R.string.basic_ensure, selected, limit)
    }
}