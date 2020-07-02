package com.cralos.cleanarchitecture.business.interactors.notelist

import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.NoteFactory
import com.cralos.cleanarchitecture.di.DependencyContainer

class InsertNewNoteTest {

    //system in test
    private val insertNewNote: InsertNewNote

    //dependencies
    private val dependencyContainer:DependencyContainer
    private val noteCacheDataSource:NoteCacheDataSource
    private val noteNetworkDataSource:NoteNetworkDataSource
    private val noteFactory:NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()

        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory= dependencyContainer.noteFactory

        insertNewNote = InsertNewNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource,
            noteFactory = noteFactory
        )
    }


}