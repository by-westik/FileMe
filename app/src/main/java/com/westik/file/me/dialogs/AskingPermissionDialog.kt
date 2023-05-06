package com.westik.file.me.dialogs

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.Global.getString
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.westik.file.me.R
import java.lang.Exception

class AskingPermissionDialog {

    fun createDialog(fragment: Fragment, launcher: ActivityResultLauncher<String>) {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(fragment.requireContext().resources.getString(R.string.dialog_title))
            .setMessage(fragment.requireContext().resources.getString(R.string.dialog_message))
            .setPositiveButton(fragment.requireContext().resources.getString(R.string.permission_allow)) { dialog, _ ->
                requestPermission(fragment, launcher)
                dialog.dismiss()
            }
            .show()
    }


    private fun requestPermission(fragment: Fragment, launcher: ActivityResultLauncher<String>){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
                intent.data = uri
                fragment.startActivity(intent)
            }
            catch (e: Exception){
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                fragment.startActivity(intent)
            }
        }
        else{
            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}