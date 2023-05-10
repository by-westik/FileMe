package com.westik.file.me.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.westik.file.me.helpers.StorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider

class FileRoomDatabaseCallback(private val provider: Provider<FileDao>) : RoomDatabase.Callback() {

    private val coroutineScopeForAllDb = CoroutineScope(SupervisorJob())

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        coroutineScopeForAllDb.launch {
            populateDatabase()
        }
    }

    private suspend fun populateDatabase() {
        provider.get().insertAll(StorageHelper.breadthFirstSearchFiles())
    }
}