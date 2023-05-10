package com.westik.file.me.fragments

import android.content.ContentValues.PARCELABLE_WRITE_RETURN_VALUE
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.westik.file.me.models.FileItem
import com.westik.file.me.viewmodels.FileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.Collections


@AndroidEntryPoint
class HomeFragment : Fragment() {



    private val viewModel : FileViewModel by viewModels()

    private var files: List<FileItem> = listOf()
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
        setFilterData()
        return view
    }

    private fun click(file: FileItem) {
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
                    fileAdapter.updateAdapter(it)
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
            val itemClick = { file: FileItem -> click(file)}
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

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "OnStop")
    }

    // TODO вынести куда-то создание диалога потом возможно
    private fun setFilterData() {

       binding.toolbar.setOnMenuItemClickListener {
           val dialogView = FilterBinding.inflate(layoutInflater)
           bottomSheetDialog = BottomSheetDialog(requireContext())
           bottomSheetDialog.setContentView(dialogView.root)
           bottomSheetDialog.show()

           var type: Comparator<File> = SorterClass.sortByName
           var ascDesc = true
           dialogView.ascDesc.setOnClickListener { it ->
               val rotateAnimation = AnimationHelper.createRotateAnimation()

               viewModel.updateCurrentFiles(currentPath,type, ascDesc)
               viewModel.currentFiles.observe(viewLifecycleOwner) {list ->
                   fileAdapter.updateAdapter(list)
               }
               ascDesc = !ascDesc
               it.startAnimation(rotateAnimation)
           }

           // TODO посмотреть про notifyDataSetChanged
           dialogView.filterGroup.setOnCheckedChangeListener {_,  checkedId ->
               when (checkedId) {
                   R.id.name -> {
                       type = SorterClass.sortByName
                   }
                   R.id.size -> {
                       type = SorterClass.sortBySize
                   }

                   R.id.date -> {
                       type = SorterClass.sortByDate
                   }

                   R.id.type -> {
                       type = SorterClass.sortByType
                   }
               }
               viewModel.updateCurrentFiles(currentPath, type)
               viewModel.currentFiles.observe(viewLifecycleOwner) {
                   fileAdapter.updateAdapter(it)
               }

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