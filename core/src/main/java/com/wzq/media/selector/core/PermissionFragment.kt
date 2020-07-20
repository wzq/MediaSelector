package com.wzq.media.selector.core

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * create by wzq on 2020/7/20
 *
 */
class PermissionFragment : Fragment() {
    companion object {
        const val TAG = "PermissionFragment"
        fun request(
            manager: FragmentManager?,
            perms: Array<String> = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
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