package com.westik.file.me.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "file_id")
    val id: Int,
    @ColumnInfo(name = "absolute_path") val absolutePath: String,
    @ColumnInfo(name = "hash_code") val hashC0de: Long
)
