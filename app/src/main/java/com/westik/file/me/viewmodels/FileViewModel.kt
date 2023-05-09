package com.westik.file.me.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.westik.file.me.helpers.Constants

import com.westik.file.me.models.FileEntity
import com.westik.file.me.data.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(private val fileRepository: FileRepository) : ViewModel() {

    var currentFiles: LiveData<List<FileEntity>> = fileRepository.getFilesFromDirectory(Constants.BASE_PATH).asLiveData()


    fun updateCurrentFiles(path: String) {
        Log.d(TAG, "START UPDATE FILES" )
        currentFiles =  fileRepository.getFilesFromDirectory(path).asLiveData()
        Log.d(TAG, "END UPDATE FILES" )
    }


    fun updateHashCode(files: List<Pair<Int, Int>>) {
        Log.d(TAG, "START UPDATE HASHCODE")
        viewModelScope.launch {
            files.forEach {
                Log.d(TAG, "UPDATED")
                fileRepository.updateHashCode(it.first, it.second)
            }
        }
        Log.d(TAG, "END UPDATE HASHCODE")
    }

}