package com.westik.file.me.di

import android.content.Context
import com.westik.file.me.data.db.FileDao
import com.westik.file.me.data.db.FileRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context, provider: Provider<FileDao>): FileRoomDatabase =
        FileRoomDatabase.getDatabase(context, provider)

    @Provides
    fun provideDao(database: FileRoomDatabase): FileDao {
        return database.getFileDao()
    }
}