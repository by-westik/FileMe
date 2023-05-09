package com.westik.file.me.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileEntity (

    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "size") val size: Long,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "absolute_path") val absolutePath: String,
    @ColumnInfo(name = "parent_path") val parentPath: String?,
    @ColumnInfo(name = "is_directory") val isDirectory: Boolean,
    @ColumnInfo(name = "is_directory_empty") val isDirectoryEmpty: Boolean,
    @ColumnInfo(name = "can_read") val canRead: Boolean
)
