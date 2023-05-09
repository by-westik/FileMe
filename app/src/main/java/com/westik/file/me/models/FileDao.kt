package com.westik.file.me.models

import android.content.ContentValues.TAG
import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.io.File
import kotlin.random.Random

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFile(file: FileEntity)

    @Query("SELECT * FROM files")
    fun getAllFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE parent_path = :path")
    fun getFilesFromDirectory(path: String): Flow<List<FileEntity>>
    @Query("UPDATE files SET hash_code = :hashCode where file_id = :id")
    suspend fun updateHashCodes(hashCode: Int, id: Int)


    @Query("DELETE FROM files")
    suspend fun deleteAll()

    suspend fun insertFiles(files: List<FileEntity>) {
        files.forEach {
            insertFile(it)
        }
    }
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg file: FileEntity)
}