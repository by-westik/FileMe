package com.westik.file.me.data

import android.content.Context
import androidx.annotation.WorkerThread
import com.westik.file.me.data.db.FileDao
import com.westik.file.me.helpers.StorageHelper
import com.westik.file.me.models.FileEntity
import com.westik.file.me.models.FileItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FileRepository @Inject constructor(private val fileDao: FileDao,
                                         private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {


    @Suppress("RedunantSuperModifier")
    @WorkerThread
    suspend fun insert(file: FileEntity) {
        fileDao.insertFile(file)
    }

    suspend fun saveHashesToBd(path: String) {
        val files = StorageHelper.breadthFirstSearchFiles(path)
        fileDao.insertAll(files)
    }


    fun getFilesFromDirectory(path: String) : Flow<List<FileItem>> = flow {
        val rawFiles = StorageHelper.getFilesFromPath(path).filter { !it.isHidden }.sortedBy { it.name }


        val fileList = coroutineScope {
            rawFiles.map {
                async {
                    FileItem(
                        name = it.name,
                        lastModified = it.lastModified(),
                        size = it.length(),
                        absolutePath = it.absolutePath,
                        canRead = it.canRead(),
                        isDirectory = it.isDirectory,
                        type = it.extension,
                        isDirectoryEmpty = if (it.isDirectory) it.listFiles()
                            .isNullOrEmpty() else false,
                        hashC0de = it.lastModified().hashCode()
                    )
                }
            }
        }
        emit(fileList.awaitAll())
    }.flowOn(dispatcher)

}


