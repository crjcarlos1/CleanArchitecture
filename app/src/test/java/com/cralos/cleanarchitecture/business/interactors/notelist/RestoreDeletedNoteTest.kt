package com.cralos.cleanarchitecture.business.interactors.notelist

import com.cralos.cleanarchitecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.cralos.cleanarchitecture.business.data.cache.FORCE_GENERAL_FAILURE
import com.cralos.cleanarchitecture.business.data.cache.FORCE_NEW_NOTE_EXCEPTION
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.NoteFactory
import com.cralos.cleanarchitecture.business.domain.state.DataState
import com.cralos.cleanarchitecture.business.interactors.notelist.RestoreDeletedNote.Companion.RESTORED_NOTE_FAIL
import com.cralos.cleanarchitecture.business.interactors.notelist.RestoreDeletedNote.Companion.RESTORE_NOTE_SUCCESS
import com.cralos.cleanarchitecture.di.DependencyContainer
import com.cralos.cleanarchitecture.framework.presentation.notelist.state.NoteListStateEvent
import com.cralos.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

/*
Test cases:
1. restoreNote_success_confirmCacheAndNetworkUpdated()
    a) create a new note and insert it into the "deleted" node of network
    b) restore that note
    c) Listen for success msg RESTORE_NOTE_SUCCESS from flow
    d) confirm note is in the cache
    e) confirm note is in the network "notes" node
    f) confirm note is not in the network "deletes" node
2. restoreNote_fail_confirmCacheAndNetworkUnchanged()
    a) create a new note and insert it into the "deleted" node of network
    b) restore that note (force a failure)
    c) Listen for success msg RESTORE_NOTE_FAILED from flow
    d) confirm note is not in the cache
    e) confirm note is not in the network "notes" node
    f) confirm note is in the network "deletes" node
3. throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    a) create a new note and insert it into the "deleted" node of network
    b) restore that note (force an exception)
    c) Listen for success msg CACHE_ERROR_UNKNOWN from flow
    d) confirm note is not in the cache
    e) confirm note is not in the network "notes" node
    f) confirm note is in the network "deletes" node
 */
@InternalCoroutinesApi
class RestoreDeletedNoteTest {
    //system in test
    private val restoreDeletedNote: RestoreDeletedNote

    //dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteFactory = dependencyContainer.noteFactory
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource

        restoreDeletedNote = RestoreDeletedNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }

    @Test
    fun restoreNote_success_confirmCacheAndNetworkUpdated() = runBlocking {
        //create a new note and insert into 'deletes' node
        val restoredNote = noteFactory.createSingleNote(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )

        noteNetworkDataSource.insertDeletedNote(note = restoredNote)

        //confirm that note is in the 'deletes' node before restauration
        var deletedNotes = noteNetworkDataSource.getDeletedNote()
        assertTrue { deletedNotes.contains(restoredNote) }

        restoreDeletedNote.restoreDeletedNote(
            note = restoredNote,
            stateEvent = NoteListStateEvent.RestoreDeletedNoteEvent(restoredNote)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(value?.stateMessage?.response?.message, RESTORE_NOTE_SUCCESS)
            }
        })

        //confirm note is in the cache
        val noteInCache = noteCacheDataSource.searchNoteById(restoredNote.id)
        assertTrue { noteInCache == restoredNote }

        // confirm note is in the network 'notes' node
        val noteInNetwork = noteNetworkDataSource.searchNote(restoredNote)
        assertTrue { noteInNetwork == restoredNote }

        //confirm note is NOT in the network 'deletes' node
        deletedNotes = noteNetworkDataSource.getDeletedNote()
        assertFalse { deletedNotes.contains(restoredNote) }
    }

    @Test
    fun restoreNote_fail_confirmCacheAndNetworkUnchanged() = runBlocking {
        //create a new note and insert into 'deletes' node
        val restoredNote = noteFactory.createSingleNote(
            id = FORCE_GENERAL_FAILURE,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )

        noteNetworkDataSource.insertDeletedNote(note = restoredNote)

        //confirm that note is in the 'deletes' node before restauration
        var deletedNotes = noteNetworkDataSource.getDeletedNote()
        assertTrue { deletedNotes.contains(restoredNote) }

        restoreDeletedNote.restoreDeletedNote(
            note = restoredNote,
            stateEvent = NoteListStateEvent.RestoreDeletedNoteEvent(restoredNote)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(value?.stateMessage?.response?.message, RESTORED_NOTE_FAIL)
            }
        })

        //confirm note is NOT in the cache
        val noteInCache = noteCacheDataSource.searchNoteById(restoredNote.id)
        assertTrue { noteInCache == null }

        // confirm note is NOT in the network 'notes' node
        val noteInNetwork = noteNetworkDataSource.searchNote(restoredNote)
        assertTrue { noteInNetwork == null }

        //confirm note is in the network 'deletes' node
        deletedNotes = noteNetworkDataSource.getDeletedNote()
        assertTrue { deletedNotes.contains(restoredNote) }

    }

    @Test
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged() = runBlocking {
        //create a new note and insert into 'deletes' node
        val restoredNote = noteFactory.createSingleNote(
            id = FORCE_NEW_NOTE_EXCEPTION,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )

        noteNetworkDataSource.insertDeletedNote(note = restoredNote)

        //confirm that note is in the 'deletes' node before restauration
        var deletedNotes = noteNetworkDataSource.getDeletedNote()
        assertTrue { deletedNotes.contains(restoredNote) }

        restoreDeletedNote.restoreDeletedNote(
            note = restoredNote,
            stateEvent = NoteListStateEvent.RestoreDeletedNoteEvent(restoredNote)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN) ?: false
                )
            }
        })

        //confirm note is NOT in the cache
        val noteInCache = noteCacheDataSource.searchNoteById(restoredNote.id)
        assertTrue { noteInCache == null }

        // confirm note is NOT in the network 'notes' node
        val noteInNetwork = noteNetworkDataSource.searchNote(restoredNote)
        assertTrue { noteInNetwork == null }

        //confirm note is in the network 'deletes' node
        deletedNotes = noteNetworkDataSource.getDeletedNote()
        assertTrue { deletedNotes.contains(restoredNote) }
    }

}
















