// com/example/collegecompanion/di/DatabaseModule.kt
package com.example.collegecompanion.di

import android.content.Context
import androidx.room.Room
import com.example.collegecompanion.data.local.AppDatabase
import com.example.collegecompanion.data.local.TaskDao
import com.example.collegecompanion.data.repository.TaskRepository
import com.example.collegecompanion.data.repository.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao =
        database.taskDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}