package com.westik.file.me.models


data class FileItem (
    val name: String,
    val lastModified: Long,
    val size: Long,
    val type: String,
    val absolutePath: String,
    val isDirectory: Boolean,
    val isDirectoryEmpty: Boolean,
    val canRead: Boolean,
    val isModified: Boolean
)
