package com.westik.file.me.data

import android.content.ContentValues.TAG
import android.util.Log
import androidx.annotation.WorkerThread
import com.westik.file.me.data.db.FileDao
import com.westik.file.me.models.FileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FileRepository @Inject constructor(private val fileDao: FileDao) {

    val allFiles: Flow<List<FileEntity>> = fileDao.getAllFiles()

    @Suppress("RedunantSuperModifier")
    @WorkerThread
    suspend fun insert(file: FileEntity) {
        fileDao.insertFile(file)
    }

    suspend fun updateHashCodes(hashCode: Int, id: Int) {
        Log.d(TAG, "START UPATE HASHCODE")
        fileDao.updateHashCodes(hashCode, id)
        Log.d(TAG, "START UPATE HASHCODE")
    }

    fun getFilesFromDirectory(path: String) : Flow<List<FileEntity>> {

        return fileDao.getFilesFromDirectory(path)
    }


}