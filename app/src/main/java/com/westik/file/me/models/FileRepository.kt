package com.westik.file.me.models

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class FileRepository(private val fileDao: FileDao) {

    val allFiles: Flow<List<FileEntity>> = fileDao.getAllFiles()

    @Suppress("RedunantSuperModifier")
    @WorkerThread
    suspend fun insert(file: FileEntity) {
        fileDao.insertFile(file)
    }


    fun getFilesFromDirectory(path: String) : Flow<List<FileEntity>> {


        return fileDao.getFilesFromDirectory(path)
    }


}