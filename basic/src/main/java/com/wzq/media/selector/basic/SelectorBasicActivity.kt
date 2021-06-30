package com.wzq.media.selector.basic

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.wzq.media.selector.basic.databinding.ActivityBasicSelectorBinding
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.PermissionFragment
import com.wzq.media.selector.core.config.MimeType
import com.wzq.media.selector.core.config.SelectorType
import com.wzq.media.selector.core.model.MediaData
import com.wzq.media.selector.core.source.ImageSource

/**
 * create by wzq on 2021/5/21
 *
 */
class SelectorBasicActivity : AppCompatActivity() {

    companion object {

        init {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            }
        }

        fun open(
            activity: Activity?,
        ) {
            activity ?: return
            val intent = Intent(activity, SelectorBasicActivity::class.java)
            activity.startActivityForResult(intent, MediaSelector.SELECTOR_REQ)
        }
    }

    private val cameraReqCode = 0x11
    private var savedPictureUri: Uri? = null

    private lateinit var binding: ActivityBasicSelectorBinding
    private val listAdapter = SelectorAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.listView.adapter = listAdapter
        binding.floatingActionButton.setOnClickListener {
            val data = Intent().putParcelableArrayListExtra("data", listAdapter.selectedList)
            setResult(RESULT_OK, data)
            finish()
        }

        refreshSource()
    }

    //刷新数据
    private fun refreshSource() {
        //检查读写权限，SDK 28以上只需读取权限
        whenPermissionPassed {
            val scanPath = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.path
            val mime = arrayOf(MimeType.JPEG.value, MimeType.PNG.value)
            //扫描相关文件后，刷新别表
            MediaScannerConnection.scanFile(this, arrayOf(scanPath), mime) { _, _ ->
                val images = ImageSource().getMediaSource(this)
                runOnUiThread {
                    buildMenus(images)
                    listAdapter.submitList(images)
                }
            }
        }
    }

    private fun whenPermissionPassed(block: () -> Unit) {
        PermissionFragment.requestCompat(supportFragmentManager) {
            if (it) {
                block()
            } else {
                Toast.makeText(this, "未获得相关权限", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openCamera() {
        val photoURI = contentResolver.let {
            val volumeName = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            } else {
                MediaStore.VOLUME_EXTERNAL
            }
            val collection = MediaStore.Images.Media.getContentUri(volumeName)
            val name = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "ms-picture-${System.currentTimeMillis()}.jpg"
                )
            }
            it.insert(collection, name)
        }

        savedPictureUri = photoURI
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(takePictureIntent, cameraReqCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraReqCode && resultCode == RESULT_OK) {
            refreshSource()
        } else {
            savedPictureUri?.also {
                contentResolver.delete(it, null, null)
                savedPictureUri = null
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_selector, menu)
        return true
    }

    private val popupMenu by lazy {
        PopupMenu(this, binding.toolbar)
    }


    //生成类目
    private fun buildMenus(images: List<MediaData>) {
        val data = mutableListOf<Pair<String, List<MediaData>>>()
        data.clear()
        data.add(Pair("全部图片", images))
        val columns = images.groupBy { et ->
            et.dirName ?: "未知"
        }.toList().sortedByDescending { p -> p.second.size }
        data.addAll(columns)
        popupMenu.menu.clear()
        data.forEachIndexed { index, pair ->
            val str = "${pair.first}  (${pair.second.size})"
            popupMenu.menu.add(0, index, index, str)
        }
        popupMenu.setOnMenuItemClickListener {
            title = data[it.itemId].first
            listAdapter.submitList(data[it.itemId].second)
            true
        }
        title = "全部图片"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.image_filter -> {
                popupMenu.show()
                true
            }
            R.id.open_camera -> {
                openCamera()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}