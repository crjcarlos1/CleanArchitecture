package com.cralos.cleanarchitecture.business.interactors.splash

import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.business.domain.model.NoteFactory
import com.cralos.cleanarchitecture.di.DependencyContainer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SyncDeletedNotesTest {

    /**
     * Test cases:
     * 1. deleteNetworkNotes_confirmCacheSync()
     *      a) select some notes for deleting from netowrk
     *      b) delete from network
     *      c) perform sync
     *      d) confim notes from cache  were deleted
     *
     */

    //system in test
    private val syncDeletedNotes: SyncDeletedNotes

    //dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()

        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory

        syncDeletedNotes = SyncDeletedNotes(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }

    @Test
    fun deleteNetworkNotes_confirmCacheSync() = runBlocking {
        // select some notes to be deleted from cache
        val networkNotes = noteNetworkDataSource.getAllNotes()
        val notesToDelete: ArrayList<Note> = ArrayList()

        for (note in networkNotes) {
            notesToDelete.add(note)
            noteNetworkDataSource.deleteNote(note.id)
            if (notesToDelete.size > 4) {
                break
            }
        }

        syncDeletedNotes.syncDeletedNotes()

        //confirm
        for (note in notesToDelete){
            val cacheNote = noteCacheDataSource.searchNoteById(note.id)
            assertTrue{cacheNote== null}
        }

    }


}