package com.westik.file.me.data.db

import android.content.ContentValues.TAG
import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.westik.file.me.models.FileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFile(file: FileEntity)

    @Query("SELECT * FROM files")
    suspend fun getAll() : List<FileEntity>

    @Query("SELECT * from files where file_id = :id")
    suspend fun getFile(id: Int) : FileEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(files: List<FileEntity>) {
        files.forEach {
            Log.d(TAG, "insert it.hashCode = ${it.hashC0de}")
            insertFile(it)
        }
    }

    @Query("DELETE FROM files")
    suspend fun deleteAll()

}