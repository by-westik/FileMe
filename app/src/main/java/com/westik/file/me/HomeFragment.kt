package com.westik.file.me

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.radiobutton.MaterialRadioButton
import com.westik.file.me.adapters.FileAdapter
import com.westik.file.me.databinding.FilterBinding
import com.westik.file.me.databinding.FragmentHomeBinding
import com.westik.file.me.dialogs.AskingPermissionDialog
import com.westik.file.me.helpers.FileItemDecorator
import com.westik.file.me.helpers.Files
import com.westik.file.me.helpers.SorterClass
import com.westik.file.me.models.FileModel
import java.io.File
import java.util.Collections
import java.util.Spliterator


class HomeFragment : Fragment() {

    private var files: List<FileModel> = Files.getFiles()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fileAdapter: FileAdapter
    private lateinit var launcher: ActivityResultLauncher<String>

    private lateinit var currentPath: String

    private var startDegress = 0.0f
    private var endDegrees = 180.0f
    private lateinit var name: MaterialRadioButton
    private lateinit var size: MaterialRadioButton
    private lateinit var date: MaterialRadioButton
    private lateinit var type: MaterialRadioButton
    lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        showPermissionDialog()
        setupRecyclerView(files)
        setFilterData(files)
        // TODO вынести в константу
        currentPath = "/storage/emulated/0"
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val file = File(currentPath).parentFile
                if (file != null && file.canRead()) {
                    fileAdapter.directoryOnClick(Files.getFiles(file.absolutePath))
                    currentPath = file.absolutePath
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun setupRecyclerView(files: List<FileModel>) {
        Log.d(TAG,"files size: ${files.size}")
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fileAdapter = FileAdapter(files, this)
        fileAdapter.onItemClick = {path, _ ->
                currentPath = path
        }
        binding.rvFiles.layoutManager = linearLayoutManager
        binding.rvFiles.adapter = fileAdapter
        ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let {
            FileItemDecorator(it)
        } ?.let {
            binding.rvFiles.addItemDecoration(it)
        }

    }

   fun setFilterData(files: List<FileModel> ) {
       binding.toolbar.setOnMenuItemClickListener {
           val dialogView = FilterBinding.inflate(layoutInflater)

           name = dialogView.name
           date = dialogView.date
           size = dialogView.size
           type = dialogView.type

           bottomSheetDialog = BottomSheetDialog(requireContext())
           bottomSheetDialog.setContentView(dialogView.root)
           bottomSheetDialog.show()

           // TODO вынести анимацию куда-то
           dialogView.ascDesc.setOnClickListener {
               val rotateAnimation = RotateAnimation(startDegress, endDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
               rotateAnimation.interpolator = DecelerateInterpolator()
               rotateAnimation.repeatCount = 0
               rotateAnimation.duration = 800
               rotateAnimation.fillAfter = true
               val tmp = startDegress
               startDegress = endDegrees
               endDegrees = tmp
               Collections.reverse(files)
               fileAdapter.notifyDataSetChanged()
               it.startAnimation(rotateAnimation)
           }
           // TODO посмотреть про notifyDataSetChanged
           dialogView.filterGroup.setOnCheckedChangeListener { _, checkedId ->
               when (checkedId) {
                   R.id.name -> {
                       Collections.sort(files, SorterClass.sortByName)
                       fileAdapter.notifyDataSetChanged()
                   }
                   R.id.size -> {
                       Collections.sort(files, SorterClass.sortBySize)
                       fileAdapter.notifyDataSetChanged()
                   }

                   R.id.date -> {
                       Collections.sort(files, SorterClass.sortByDate)
                       fileAdapter.notifyDataSetChanged()
                   }

                   R.id.type -> {
                       Collections.sort(files, SorterClass.sortByType)
                       fileAdapter.notifyDataSetChanged()
                   }
               }
           }


           return@setOnMenuItemClickListener true
       }
   }

    private fun showPermissionDialog() {
        if (!isPermissionGranted(requireContext())) {
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