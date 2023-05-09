package com.westik.file.me.fragments

import android.os.Bundle
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

    private var files: List<FileEntity> = StorageHelper.getFilesFromPath()
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
        setupRecyclerView()
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
                viewModel.currentFiles.observe(viewLifecycleOwner) { files ->
                    files.forEach {
                    }
                    fileAdapter.updateAdapter(files)
                }
            }
        } else {
            this.startActivity(FileHelper.openFile(file, requireContext()))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val file = File(currentPath).parentFile
                if (file != null && file.canRead()) {
                    viewModel.updateCurrentFiles(file.absolutePath)
                    viewModel.currentFiles.observe(viewLifecycleOwner) {
                        fileAdapter.updateAdapter(it)
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
    }
    private fun setupRecyclerView() {
        if (AskingPermissionDialog().isPermissionGranted(requireContext())) {
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val itemClick = { file: FileEntity -> click(file)}
            fileAdapter = FileAdapter(files, this, itemClick)

            binding.rvFiles.layoutManager = linearLayoutManager
            binding.rvFiles.adapter = fileAdapter
            viewModel.currentFiles.observe(viewLifecycleOwner) {
                fileAdapter.updateAdapter(it)
            }

            ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let {
                FileItemDecorator(it)
            } ?.let {
                binding.rvFiles.addItemDecoration(it)
            }

        }


    }

    // TODO вынести куда-то создание диалога потом возможно
   fun setFilterData() {
       binding.toolbar.setOnMenuItemClickListener {
           val dialogView = FilterBinding.inflate(layoutInflater)
           bottomSheetDialog = BottomSheetDialog(requireContext())
           bottomSheetDialog.setContentView(dialogView.root)
           bottomSheetDialog.show()

           // TODO исправить баг с тем, что при сортироке папок сортируется главная папка
           // TODO исправить, что при первом запуксе и первой сортировке пропадают файлы почему-то

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
                       fileAdapter.updateAdapter(files)
                   }
                   R.id.size -> {
                       Collections.sort(files, SorterClass.sortBySize)
                       fileAdapter.updateAdapter(files)
                   }

                   R.id.date -> {
                       Collections.sort(files, SorterClass.sortByDate)
                       fileAdapter.updateAdapter(files)
                   }

                   R.id.type -> {
                       Collections.sort(files, SorterClass.sortByType)
                       fileAdapter.updateAdapter(files)
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
        } else {
            setupRecyclerView()
        }
    }



}