package com.westik.file.me.fragments

import android.content.ContentValues.PARCELABLE_WRITE_RETURN_VALUE
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.westik.file.me.R
import com.westik.file.me.adapters.FileAdapter
import com.westik.file.me.databinding.FilterBinding
import com.westik.file.me.databinding.FragmentHomeBinding
import com.westik.file.me.dialogs.AskingPermissionDialog
import com.westik.file.me.helpers.AnimationHelper
import com.westik.file.me.helpers.Constants
import com.westik.file.me.helpers.FileHelper
import com.westik.file.me.helpers.FileItemDecorator
import com.westik.file.me.helpers.StorageHelper
import com.westik.file.me.helpers.SorterClass
import com.westik.file.me.models.FileEntity
import com.westik.file.me.viewmodels.FileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Collections


@AndroidEntryPoint
class HomeFragment : Fragment() {



    private val viewModel : FileViewModel by viewModels()

    private var files: List<FileEntity> = listOf()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fileAdapter: FileAdapter
    private lateinit var launcher: ActivityResultLauncher<String>

    private var currentPath: String = Constants.BASE_PATH
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var updatedFiles = mutableListOf<FileEntity>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        showPermissionDialog()
        setFilterData()
        return view
    }

    private fun click(file: FileEntity) {
        if (!file.canRead) {
            Toast.makeText(requireContext(), "Доступ запрещен", Toast.LENGTH_LONG).show()

        }
        if (file.isDirectory) {
            if (file.isDirectoryEmpty) {
                Toast.makeText(requireContext(), "Пустая папка", Toast.LENGTH_SHORT).show()
            } else {
                currentPath = file.absolutePath
                viewModel.updateCurrentFiles(currentPath)
                viewModel.currentFiles.observe(viewLifecycleOwner) {
                    files = it
                    fileAdapter.updateAdapter(files)
                }
            }
        } else {
            this.startActivity(FileHelper.openFile(file, requireContext()))
        }
    }

    private fun change(file: FileEntity) {
        updatedFiles.add(file)
        Log.d(TAG, "File added")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val file = File(currentPath).parentFile
                if (file != null && file.canRead()) {
                    viewModel.updateCurrentFiles(file.absolutePath)
                    viewModel.currentFiles.observe(viewLifecycleOwner) {
                        files = it
                        fileAdapter.updateAdapter(files)
                    }
                    currentPath = file.absolutePath
                }
            }
        }
        if (AskingPermissionDialog().isPermissionGranted(requireContext())) {
            setupRecyclerView()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onResume() {
        super.onResume()
        if (AskingPermissionDialog().isPermissionGranted(requireContext())) {
            setupRecyclerView()
        }
        val list = mutableListOf<Pair<Int, Int>>()
        updatedFiles.forEach {
            list.add(Pair(it.hashCode(), it.id))
        }
        viewModel.updateHashCode(list)
    }
    private fun setupRecyclerView() {
        if (AskingPermissionDialog().isPermissionGranted(requireContext())) {
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val changeHashCode = {file : FileEntity -> change(file)}
            val itemClick = { file: FileEntity -> click(file)}
            fileAdapter = FileAdapter(files, this, itemClick, changeHashCode)

            binding.rvFiles.layoutManager = linearLayoutManager
            binding.rvFiles.adapter = fileAdapter
            viewModel.currentFiles.observe(viewLifecycleOwner) {
                files = it
                fileAdapter.updateAdapter(files)
            }

            ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let {
                FileItemDecorator(it)
            } ?.let {
                binding.rvFiles.addItemDecoration(it)
            }

        }


    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "OnStop")
        val list = mutableListOf<Pair<Int, Int>>()
        updatedFiles.forEach {
            list.add(Pair(File(it.absolutePath).lastModified().hashCode(), it.id))
        }
        viewModel.updateHashCode(list)
    }

    // TODO вынести куда-то создание диалога потом возможно
    private fun setFilterData() {
       binding.toolbar.setOnMenuItemClickListener {
           val dialogView = FilterBinding.inflate(layoutInflater)
           bottomSheetDialog = BottomSheetDialog(requireContext())
           bottomSheetDialog.setContentView(dialogView.root)
           bottomSheetDialog.show()

           dialogView.ascDesc.setOnClickListener {
               val rotateAnimation = AnimationHelper.createRotateAnimation()
               Collections.reverse(files)
               fileAdapter.updateAdapter(files)
               it.startAnimation(rotateAnimation)
           }
           // TODO посмотреть про notifyDataSetChanged
           dialogView.filterGroup.setOnCheckedChangeListener { _, checkedId ->
               when (checkedId) {
                   R.id.name -> {
                       Collections.sort(files, SorterClass.sortByName)
                   }
                   R.id.size -> {
                       Collections.sort(files, SorterClass.sortBySize)
                   }

                   R.id.date -> {
                       Collections.sort(files, SorterClass.sortByDate)
                   }

                   R.id.type -> {
                       Collections.sort(files, SorterClass.sortByType)
                   }
               }
               fileAdapter.updateAdapter(files)
           }

           return@setOnMenuItemClickListener true
       }
   }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    private fun showPermissionDialog() {
        if (!AskingPermissionDialog().isPermissionGranted(requireContext())) {
            launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()){}
            AskingPermissionDialog().createDialog(this, launcher)
        }
    }



}