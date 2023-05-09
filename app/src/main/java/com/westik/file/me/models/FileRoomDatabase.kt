package com.westik.file.me.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.westik.file.me.helpers.Constants
import com.westik.file.me.helpers.StorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [FileEntity::class], version = 1, exportSchema = false)
abstract class FileRoomDatabase :  RoomDatabase() {

    abstract fun getFileDao() : FileDao

    private class FileRoomDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val fileDao = database.getFileDao()
                    //fileDao.deleteAll()

                    val files = StorageHelper.breadthFirstSearchFiles(Constants.BASE_PATH)
                    fileDao.insertAllFiles(files)
                }
            }
        }

    }

    companion object {
        @Volatile
        private var INSTANCE: FileRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FileRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FileRoomDatabase::class.java,
                    "files_database"
                ).addCallback(FileRoomDatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }
     }

}