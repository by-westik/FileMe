package com.westik.file.me.helpers

import com.westik.file.me.models.FileModel
import java.io.*

class Files {

    companion object{
        // TODO вынести путь к памяти в константу
        fun getFiles(path: String = "/storage/emulated/0") : ArrayList<FileModel>{
            var directories = mutableListOf<File>()
            var files = mutableListOf<File>()

            val filesArray = File(path).listFiles() as Array<File>
            filesArray.forEach {file ->
                if (!file.isHidden){
                    if (file.isDirectory){
                        directories.add(file)
                    } else {
                        files.add(file)
                    }
                }
            }


            directories = directories.sortedWith(compareBy {
                it.name
            }).toMutableList()

            files = files.sortedWith(compareBy {
                it.name
            }).toMutableList()

            directories.addAll(files)

            val result = arrayListOf<FileModel>()
            directories.forEach {
                result.add(
                    FileModel
                        (
                        name = it.name,
                        type = it.extension.lowercase(),
                        lastModified = it.lastModified(),
                        isDirectory = it.isDirectory,
                        absolutePath = it.absolutePath,
                        size = getFileSize(it.absolutePath))
                )
            }

            return result
        }

        private fun getFileSize(path: String): String {
            val file = File(path)
            val megaBytes = file.length().toDouble() / (1024 * 1024)
            val str = String.format("%.2f", megaBytes)
            return "$str Mb"
        }

    }

}