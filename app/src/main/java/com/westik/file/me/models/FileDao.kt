package com.westik.file.me.models

import android.content.ContentValues.TAG
import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFile(file: FileEntity)

    @Query("SELECT * FROM files")
    fun getAllFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE parent_path = :path")
    fun getFilesFromDirectory(path: String): Flow<List<FileEntity>>


    @Query("DELETE FROM files")
    suspend fun deleteAll()

    suspend fun insertAllFiles(files: List<FileEntity>) {
        Log.d(TAG, "START INSERT files size = ${files.size}")
        files.forEach {
            insertFile(it)
        }
        Log.d(TAG, "files size = ${files.size} END INSERT")
    }
}