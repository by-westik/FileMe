package com.westik.file.me.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.tabs.TabLayout.TabGravity
import com.westik.file.me.helpers.Constants

import com.westik.file.me.models.FileEntity
import com.westik.file.me.models.FileRepository
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(private val fileRepository: FileRepository) : ViewModel() {

    val allFiles: LiveData<List<FileEntity>> = fileRepository.allFiles.asLiveData()
    private var listForAdapter: MutableLiveData<List<FileEntity>> = MutableLiveData()
    var currentFiles: LiveData<List<FileEntity>> = fileRepository.getFilesFromDirectory(Constants.BASE_PATH).asLiveData()

    fun updateCurrentFiles(path: String) {
        Log.d(TAG, "START UPDATE FILES" )
        currentFiles =  fileRepository.getFilesFromDirectory(path).asLiveData()

        Log.d(TAG, "END UPDATE FILES" )
    }

    fun insertFile(file: FileEntity) = viewModelScope.launch {
        fileRepository.insert(file)
    }



    fun getFiles() : List<FileEntity>? = listForAdapter.value
}