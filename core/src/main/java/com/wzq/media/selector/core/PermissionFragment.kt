package com.wzq.media.selector.core

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat

/**
 * create by wzq on 2020/7/20
 * @param perms
 *
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
 */
class PermissionFragment : androidx.fragment.app.Fragment() {
    companion object {
        const val TAG = "PermissionFragment"
        fun request(
            manager: androidx.fragment.app.FragmentManager?,
            perms: Array<String>,
            onPerm: ((Boolean) -> Unit)?
        ) {
            val args = Bundle()
            args.putStringArray("perms", perms)
            val fragment = PermissionFragment()
            fragment.arguments = args
            fragment.onPerm = onPerm
            manager?.run {
                beginTransaction().add(fragment, TAG).commit()
            }
        }
    }

    var onPerm: ((Boolean) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissions = arguments?.getStringArray("perms")
        if (permissions.isNullOrEmpty()) return
        if (check(permissions)) {
            onPerm?.invoke(true)
        } else {
            requestPermissions(permissions, MediaSelector.SELECTOR_PERM)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MediaSelector.SELECTOR_PERM && grantResults.isNotEmpty()) {
            val isGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (isGranted) {
                onPerm?.invoke(true)
            } else {
                onPerm?.invoke(false)
            }
        }
    }

    private fun check(permissions: Array<String>): Boolean {
        val context = requireContext()
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}