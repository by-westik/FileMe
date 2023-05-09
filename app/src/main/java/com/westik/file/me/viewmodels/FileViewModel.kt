package com.westik.file.me.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.westik.file.me.helpers.Constants

import com.westik.file.me.models.FileEntity
import com.westik.file.me.data.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(private val fileRepository: FileRepository) : ViewModel() {

    var allFiles: LiveData<List<FileEntity>> = fileRepository.allFiles.asLiveData()
    var currentFiles: LiveData<List<FileEntity>> = fileRepository.getFilesFromDirectory(Constants.BASE_PATH).asLiveData()

    fun updateCurrentFiles(path: String) {
        Log.d(TAG, "START UPDATE FILES" )
        currentFiles =  fileRepository.getFilesFromDirectory(path).asLiveData()
        Log.d(TAG, "END UPDATE FILES" )
    }

}