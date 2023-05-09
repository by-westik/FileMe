package com.westik.file.me.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.westik.file.me.models.FileEntity
import javax.inject.Provider

@Database(entities = [FileEntity::class], version = 1, exportSchema = false)
abstract class FileRoomDatabase :  RoomDatabase() {

    abstract fun getFileDao() : FileDao

  /*  private class FileRoomDatabaseCallback(
        private val scope: CoroutineScope,
        private val context: Context
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
               // scope.launch {
                    val fileDao = database.getFileDao()
                    Log.d(TAG, "Start insert")
                    fileDao.insertFiles(StorageHelper.breadthFirstSearchFiles())
                    Log.d(TAG, "end insert")

               // }
            }
        }

    }*/

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