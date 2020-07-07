package com.cralos.cleanarchitecture.di

import androidx.room.Room
import com.cralos.cleanarchitecture.framework.datasource.cache.database.NoteDataBase
import com.cralos.cleanarchitecture.framework.presentation.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object TestModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDb(app: TestBaseApplication) : NoteDataBase{
        return Room.inMemoryDatabaseBuilder(app,NoteDataBase::class.java).fallbackToDestructiveMigration().build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore() : FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

}