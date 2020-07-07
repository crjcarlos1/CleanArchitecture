package com.cralos.cleanarchitecture.di

import androidx.room.Room
import com.cralos.cleanarchitecture.business.domain.model.NoteFactory
import com.cralos.cleanarchitecture.framework.datasource.cache.database.NoteDataBase
import com.cralos.cleanarchitecture.framework.datasource.data.NoteDataFactory
import com.cralos.cleanarchitecture.framework.presentation.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
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
    fun provideNoteDb(app: TestBaseApplication): NoteDataBase {
        return Room.inMemoryDatabaseBuilder(app, NoteDataBase::class.java)
            .fallbackToDestructiveMigration().build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun providesFirestoreSettings(): FirebaseFirestoreSettings {
        return FirebaseFirestoreSettings.Builder()
            .setHost("10.0.2.2:8080")
            .setSslEnabled(false)
            .setPersistenceEnabled(false)
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(settings: FirebaseFirestoreSettings): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = settings
        return firestore
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDataFactory(
        application: TestBaseApplication,
        noteFactory: NoteFactory
    ): NoteDataFactory {
        return NoteDataFactory(application, noteFactory)
    }

}