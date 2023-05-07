package com.westik.file.me.helpers

import com.westik.file.me.models.FileModel

class SorterClass() {

    // TODO возможно стоит учитывать при сортировке папка/не папка
    companion object {

        var sortByName = Comparator<FileModel> { o1, o2 -> o1.name.compareTo(o2.name) }

        val sortByType = Comparator<FileModel> { o1, o2 -> o1.type.compareTo(o2.type) }

        // TODO считать размер папок и сортировать по ним тоже
        val sortBySize = Comparator<FileModel> { o1, o2 -> (o1.size - o2.size).toInt() }

        val sortByDate = Comparator<FileModel> { o1, o2 -> (o1.lastModified - o2.lastModified).toInt() }
    }
}