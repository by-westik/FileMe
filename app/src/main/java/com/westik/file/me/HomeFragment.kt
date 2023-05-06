package com.westik.file.me

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.westik.file.me.databinding.FragmentHomeBinding
import com.westik.file.me.dialogs.AskingPermissionDialog
import java.io.File
import java.lang.Exception


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var launcher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        showPermissionDialog()
        return view
    }

    private fun showPermissionDialog() {
        if (isPermissionGranted(requireContext())) {
            Toast.makeText(requireContext(), "PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
        } else {
            launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()){}
            AskingPermissionDialog().createDialog(this, launcher)
        }
    }

    private fun isPermissionGranted(context: Context):Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return  Environment.isExternalStorageManager()
        }
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }



}