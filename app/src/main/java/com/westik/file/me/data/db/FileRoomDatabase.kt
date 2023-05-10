package com.westik.file.me.data.db

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.westik.file.me.models.FileEntity
import javax.inject.Provider

// TODO вынести название и версию в константы
@Database(entities = [FileEntity::class], version = 1, exportSchema = false)
abstract class FileRoomDatabase :  RoomDatabase() {

    abstract fun getFileDao() : FileDao

    companion object {
        @Volatile
        private var INSTANCE: FileRoomDatabase? = null

        fun getDatabase(context: Context, provider: Provider<FileDao>): FileRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FileRoomDatabase::class.java,
                    "files_database"
                ).addCallback(FileRoomDatabaseCallback(provider)).build()
                INSTANCE = instance
                instance
            }
        }
     }

}