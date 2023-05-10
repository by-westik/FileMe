package com.westik.file.me.helpers

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.material.tabs.TabLayout.TabGravity
import com.google.android.material.tabs.TabLayout.VIEW_LOG_TAG
import com.westik.file.me.models.FileEntity
import com.westik.file.me.models.FileItem
import java.io.File
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.LinkedList


class StorageHelper {

    companion object {
        /*
            При таком варианте не учитывается, что у телефона может быть дополнительная память (micro SD)
         */

                //TODO добавить в README что нельзя получить дату создания файла, можно только изменения
        fun breadthFirstSearchFiles(path: String = Constants.BASE_PATH): List<FileEntity> {
            val result = mutableListOf<FileEntity>()
            val queue = LinkedList<File>()

            val node = File(path)

            queue.add(node)
            while (!queue.isEmpty()) {
                val current = queue.poll()
                if (current != null) {
                    if (!current.listFiles().isNullOrEmpty()) {
                        current.listFiles()?.forEach {
                            if (!it.isHidden) {
                                if (it.isDirectory) {
                                    queue.add(it)
                                }
                                result.add(
                                    FileEntity(
                                        id = 0,
                                        absolutePath = it.absolutePath,
                                        hashC0de = if (it.isDirectory) 0 else it.lastModified()
                                    )
                                )
                            }
                        }
                    }
                }
            }
            return result
        }

        fun getFilesFromPath(path: String): List<File> = File(path).listFiles()?.toList() ?: emptyList()

    }
}