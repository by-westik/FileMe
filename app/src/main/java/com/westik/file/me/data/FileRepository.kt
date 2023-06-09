package com.westik.file.me.data

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.westik.file.me.data.db.FileDao
import com.westik.file.me.helpers.SorterClass
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
import java.io.File
import java.util.Collections
import javax.inject.Inject

class FileRepository @Inject constructor(private val fileDao: FileDao,
                                         private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {


    private var hashCodes: MutableList<Long> = mutableListOf()
    suspend fun createHashList() {
        hashCodes.addAll(fileDao.getAll().map { it.hashC0de })
    }

    fun getFilesFromDirectory(path: String, comparator: Comparator<File>, ascDesc: Boolean) : Flow<List<FileItem>> = flow {
        val rawFiles = StorageHelper.getFilesFromPath(path).filter { !it.isHidden }.sortedWith(comparator)
        if (ascDesc) {
            Collections.reverse(rawFiles)
        }

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
                        isModified = !hashCodes.contains(it.lastModified())
                    )
                }
            }
        }
        emit(fileList.awaitAll())
    }.flowOn(dispatcher)

}


