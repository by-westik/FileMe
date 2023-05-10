package com.westik.file.me.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.westik.file.me.helpers.Constants

import com.westik.file.me.models.FileEntity
import com.westik.file.me.data.FileRepository
import com.westik.file.me.helpers.SorterClass
import com.westik.file.me.models.FileItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(private val fileRepository: FileRepository) : ViewModel() {

    private var _currentFiles: MutableLiveData<List<FileItem>> = fileRepository.getFilesFromDirectory(Constants.BASE_PATH, SorterClass.sortByName, false).asLiveData() as MutableLiveData<List<FileItem>>
    val currentFiles: LiveData<List<FileItem>>
        get() = _currentFiles

    fun updateCurrentFiles(path: String, comparator: Comparator<File> = SorterClass.sortByName, ascDesc: Boolean = false) = viewModelScope.launch {
        _currentFiles = fileRepository.getFilesFromDirectory(path, comparator, ascDesc).asLiveData() as MutableLiveData<List<FileItem>>
    }

    init {
        viewModelScope.launch {
            fileRepository.createHashList()
        }

    }
}