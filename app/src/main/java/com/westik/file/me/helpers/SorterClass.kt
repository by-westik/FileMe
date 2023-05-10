package com.westik.file.me.helpers

import com.westik.file.me.models.FileEntity
import com.westik.file.me.models.FileItem

class SorterClass {

    // TODO возможно стоит учитывать при сортировке папка/не папка
    companion object {

        var sortByName = Comparator<FileItem> { o1, o2 -> o1.name.compareTo(o2.name) }

        val sortByType = Comparator<FileItem> { o1, o2 -> o1.type.compareTo(o2.type) }

        // TODO считать размер папок и сортировать по ним тоже
          val sortBySize = Comparator<FileItem> { o1, o2 -> (o1.size - o2.size).toInt() }

        val sortByDate = Comparator<FileItem> { o1, o2 -> (o1.lastModified - o2.lastModified).toInt() }
    }
}