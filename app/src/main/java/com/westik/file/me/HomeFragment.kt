package com.westik.file.me

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.westik.file.me.adapters.FileAdapter
import com.westik.file.me.databinding.FilterBinding
import com.westik.file.me.databinding.FragmentHomeBinding
import com.westik.file.me.dialogs.AskingPermissionDialog
import com.westik.file.me.helpers.AnimationHelper
import com.westik.file.me.helpers.Constants
import com.westik.file.me.helpers.FileItemDecorator
import com.westik.file.me.helpers.StorageHelper
import com.westik.file.me.helpers.SorterClass
import com.westik.file.me.models.FileModel
import java.io.File
import java.util.Collections


class HomeFragment : Fragment() {

    private var files: List<FileModel> = StorageHelper.getFiles()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fileAdapter: FileAdapter
    private lateinit var launcher: ActivityResultLauncher<String>

    private var currentPath: String = Constants.BASE_PATH
    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        showPermissionDialog()
        setupRecyclerView(files)
        setFilterData(files)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val file = File(currentPath).parentFile
                if (file != null && file.canRead()) {
                    fileAdapter.directoryOnClick(StorageHelper.getFiles(file.absolutePath))
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

    // TODO вынести куда-то создание диалога потом возможно
   fun setFilterData(files: List<FileModel> ) {
       binding.toolbar.setOnMenuItemClickListener {

           val dialogView = FilterBinding.inflate(layoutInflater)
           bottomSheetDialog = BottomSheetDialog(requireContext())
           bottomSheetDialog.setContentView(dialogView.root)
           bottomSheetDialog.show()

           dialogView.ascDesc.setOnClickListener {
               val rotateAnimation = AnimationHelper.createRotateAnimation()
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
        if (!AskingPermissionDialog().isPermissionGranted(requireContext())) {
            launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()){}
            AskingPermissionDialog().createDialog(this, launcher)
        }
    }



}