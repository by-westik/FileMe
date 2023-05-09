package com.westik.file.me.helpers

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.westik.file.me.data.FileRepository
import com.westik.file.me.data.db.FileDao
import com.westik.file.me.data.db.FileRoomDatabase
import com.westik.file.me.data.db.FileRoomDatabaseCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MyService() : LifecycleService(), LifecycleOwner {

    @Inject
    lateinit var repository: FileRepository


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "OnCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "OnStartCommand")
        Log.d(TAG, "START SERVICE")
    //    lifecycleScope.launch() {
            repository.allFiles.asLiveData().observe(this@MyService) {files ->
                lifecycleScope.launch {
                    files.forEach {
                        repository.updateHashCodes(it.hashCode(), it.id)
                    }
                }

          //  }
        }
        Log.d(TAG, "END SERVICE")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}