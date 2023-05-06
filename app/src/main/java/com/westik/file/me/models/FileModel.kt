package com.westik.file.me.models

data class FileModel(
    val name: String = "",
    val lastModified: Long,
    val isDirectory: Boolean,
    var absolutePath: String = "",
    val size: String
)