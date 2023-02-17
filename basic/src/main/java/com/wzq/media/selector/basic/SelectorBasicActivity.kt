package com.wzq.media.selector.basic

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.wzq.media.selector.basic.preview.PreviewActivity
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.PermissionFragment
import com.wzq.media.selector.core.config.MimeType
import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.config.SelectorType
import com.wzq.media.selector.core.model.MediaData


/**
 * create by wzq on 2020/7/16
 * basic MediaSelector
 */
class SelectorBasicActivity : AppCompatActivity() {

    companion object {

        init {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            }
        }

        fun open(
            activity: Activity?,
            type: SelectorType,
            mConfig: SelectorConfig? = SelectorConfig()
        ) {
            activity ?: return
            val intent = Intent(activity, SelectorBasicActivity::class.java)
            intent.putExtra("config", mConfig)
            intent.putExtra("type", type)
            activity.startActivityForResult(intent, MediaSelector.SELECTOR_REQ)
        }
    }


    private val titleView by lazy {
        findViewById<TextView>(R.id.toolbar_title)
    }

    private val popupMenu by lazy {
        PopupMenu(this, titleView)
    }

    private val mediaSelector by lazy {
        val type = intent.getSerializableExtra("type") as SelectorType
        MediaSelector.create(this, type).mime(MimeType.JPEG, MimeType.PNG)
    }
    private val config by lazy { intent.getParcelableExtra<SelectorConfig>("config") }

    private val adapter = SelectorAdapter()

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
        val perms: Array<String> = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        PermissionFragment.request(supportFragmentManager, perms) { hasPermission ->
            if (hasPermission) {
                init()
            } else {
                Toast.makeText(this, R.string.basic_permission, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun init() {
        val limit = config?.limit ?: 1
        val previewBtn = findViewById<TextView>(R.id.preview)
        if (config?.needPreview == true) {
            previewBtn.visibility = View.VISIBLE
            previewBtn.setOnClickListener { preview() }
        } else {
            previewBtn.visibility = View.GONE
        }
        val takePhoto = findViewById<View>(R.id.toolbar_camera)
        if (config?.needTakePhoto == true) {
            takePhoto.visibility = View.VISIBLE
            takePhoto.setOnClickListener {
                openCamera()
            }
        } else {
            takePhoto.visibility = View.GONE
        }
        val origin = findViewById<View>(R.id.origin)
        if (config?.needOrigin == true) {
            origin.visibility = View.VISIBLE
        } else {
            origin.visibility = View.GONE
        }
        val ensureBtn = findViewById<TextView>(R.id.ensure)
        ensureBtn.setOnClickListener { submitSelect() }
        ensureBtn.text = getString(R.string.basic_ensure, 0, limit)

        val listView = findViewById<RecyclerView>(R.id.listView)
        listView.layoutManager =
            GridLayoutManager(this, 3)
        listView.adapter = adapter
        adapter.setLimit(config?.limit ?: 1)
        adapter.setOnSelected { selected ->
            ensureBtn.isEnabled = selected > 0
            previewBtn.isEnabled = selected > 0
            ensureBtn.text = getString(R.string.basic_ensure, selected, limit)
        }

        refreshData()
    }

    private fun refreshData() {
        mediaSelector.querySource {
            val data = mutableListOf<Pair<String, List<MediaData>>>()
            data.add(Pair("全部", it))
            val columns = it.groupBy { et ->
                et.dirName ?: "未知"
            }.toList().sortedByDescending { p -> p.second.size }
            data.addAll(columns)
            buildMenus(data)
            selectColumn(data, 0)//default index
        }
    }

    //选择类目
    private fun selectColumn(data: List<Pair<String, List<MediaData>>>, index: Int) {
        if (index > -1 && index < data.size) {
            val item = data[index]
            titleView.text = item.first
            titleView.isSelected = false
            adapter.submitList(item.second)
        }
    }

    //生成类目
    private fun buildMenus(data: List<Pair<String, List<MediaData>>>) {
        popupMenu.menu.clear()
        data.forEachIndexed { index, pair ->
            val str = "${pair.first}  (${pair.second.size})"
            popupMenu.menu.add(0, index, index, str)
        }
        popupMenu.setOnMenuItemClickListener {
            selectColumn(data, it.itemId)
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

        val isOrigin = findViewById<CheckBox>(R.id.origin).isChecked
        val data = Intent().putParcelableArrayListExtra("data", adapter.selectedItems)
            .putExtra("isOrigin", isOrigin)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    //内容预览
    private fun preview() {
        val mediaType = intent?.getSerializableExtra("type") as? SelectorType ?: return

        val selected = adapter.selectedItems
        if (selected.isNullOrEmpty()) return
        val intent = Intent(this, PreviewActivity::class.java)
            .putParcelableArrayListExtra("data", adapter.selectedItems)
            .putExtra("type", mediaType)
        startActivity(intent)
    }


    /* handle Camera */
    private val REQUEST_TAKE_PHOTO = 0x11
    private var photoURI: Uri? = null
    private fun createPicUri(): Uri? {
        val audioCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val newPic = ContentValues().apply {
            val timeStamp = System.currentTimeMillis()
            val name = "JPEG_${timeStamp}"
            put(MediaStore.Images.Media.TITLE, name)
            put(MediaStore.Images.Media.DISPLAY_NAME, "JPEG_${timeStamp}.jpeg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_MODIFIED, timeStamp)
        }
        return contentResolver.insert(audioCollection, newPic)
    }

    private fun deletePicURI() {
        photoURI?.also {
            contentResolver.delete(it, null, null)
        }
    }

    private fun openCamera() {
        photoURI = createPicUri() ?: return
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    private fun scanFile() {
        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: return
        MediaScannerConnection.scanFile(this, arrayOf(path), arrayOf("image/jpeg")) { p, u ->
            runOnUiThread {
                refreshData()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            scanFile()
        } else {
            deletePicURI()
        }
    }
}