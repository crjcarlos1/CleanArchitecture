package com.cralos.cleanarchitecture.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.cralos.cleanarchitecture.framework.datasource.cache.database.NoteDataBase
import com.cralos.cleanarchitecture.framework.preferences.PreferencesKeys
import com.cralos.cleanarchitecture.framework.presentation.BaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


/*
    Dependencies in this class have test fakes for ui tests. See "TestModule.kt" in
    androidTest dir
 */
@ExperimentalCoroutinesApi
@FlowPreview
@Module
object ProductionModule {


    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDb(app: BaseApplication): NoteDataBase {
        return Room
            .databaseBuilder(app, NoteDataBase::class.java, NoteDataBase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharepreferences(application: BaseApplication): SharedPreferences {
        return application.getSharedPreferences(
            PreferencesKeys.NOTE_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }


}