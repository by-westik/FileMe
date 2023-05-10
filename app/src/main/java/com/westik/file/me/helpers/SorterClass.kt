package com.westik.file.me.helpers

import com.westik.file.me.models.FileEntity
import com.westik.file.me.models.FileItem
import java.io.File

class SorterClass {

    // TODO возможно стоит учитывать при сортировке папка/не папка
    companion object {

        var sortByName = Comparator<File> { o1, o2 -> o1.name.compareTo(o2.name) }

        val sortByType = Comparator<File> { o1, o2 -> o1.extension.compareTo(o2.extension) }

        // TODO считать размер папок и сортировать по ним тоже
          val sortBySize = Comparator<File> { o1, o2 -> (o1.length() - o2.length()).toInt() }

        val sortByDate = Comparator<File> { o1, o2 -> (o1.lastModified() - o2.lastModified()).toInt() }
    }
}