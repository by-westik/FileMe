package com.westik.file.me.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.westik.file.me.R
import com.westik.file.me.adapters.FileAdapter
import com.westik.file.me.databinding.FragmentChangeFilesBinding
import com.westik.file.me.databinding.FragmentHomeBinding
import com.westik.file.me.helpers.FileHelper
import com.westik.file.me.helpers.StorageHelper
import com.westik.file.me.models.FileEntity
import com.westik.file.me.viewmodels.FileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ChangeFilesFragment : Fragment() {

    private val viewModel : FileViewModel by viewModels()

    private var _binding: FragmentChangeFilesBinding? = null
    private val binding get() = _binding!!
    private var files: List<FileEntity> = StorageHelper.getFilesFromPath()
    private lateinit var fileAdapter: FileAdapter


    private fun click(file: FileEntity) {
        if (!File(file.absolutePath).canRead()) {
            Toast.makeText(requireContext(), "Доступ запрещен", Toast.LENGTH_LONG).show()

        }
        if (File(file.absolutePath).isDirectory) {
            if (File(file.absolutePath).list().isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Пустая папка", Toast.LENGTH_SHORT).show()
            } else {

            }
        } else {
            this.startActivity(FileHelper.openFile(file, requireContext()))
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeFilesBinding.inflate(inflater, container, false)
        val view = binding.root
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val itemClick = { file: FileEntity -> click(file)}
        fileAdapter = FileAdapter(files, this, itemClick)

        binding.rvFiles.layoutManager = linearLayoutManager
        binding.rvFiles.adapter = fileAdapter
        viewModel.allFiles.observe(viewLifecycleOwner) {
            files = it
            fileAdapter.updateAdapter(files)
        }
        return view
    }


}