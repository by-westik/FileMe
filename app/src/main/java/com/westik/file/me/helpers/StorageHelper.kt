package com.westik.file.me.helpers

import com.westik.file.me.models.FileModel
import java.io.*

class StorageHelper {

    companion object{
        /*
            При таком варианте не учитывается, что у телефона может быть дополнительная память (micro SD)
         */
        fun getFiles(path: String = Constants.BASE_PATH) : ArrayList<FileModel>{
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
                //TODO добавить в README что нельзя получить дату создания файла, можно только изменения
              /*  val filePath = Paths.get(it.absolutePath)
                val attributes: BasicFileAttributes = Files.readAttributes(filePath, BasicFileAttributes::class.java)
                val dateFormatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val creationTime = attributes.creationTime()
                val formatTime = creationTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(dateFormatter)
                Log.d(TAG,"file name = ${it.name} creationTime = $formatTime")
                val simpleDataFormat =  SimpleDateFormat.getDateInstance().format(Date(it.lastModified()))
                Log.d(TAG,"file name = ${it.name} lastModifedTime = $simpleDataFormat")*/
              //  it.list().size == 0

                result.add(
                    FileModel
                        (
                        name = it.name,
                        type = it.extension.lowercase(),
                        lastModified = it.lastModified(),
                        canRead = it.canRead(),
                        isDirectory = it.isDirectory,
                        isDirectoryEmpty = if (it.isDirectory) it.list().isNullOrEmpty() else false,
                        absolutePath = it.absolutePath,
                        size = it.length())
                )
            }

            return result
        }


    }

}