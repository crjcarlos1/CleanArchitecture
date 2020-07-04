package com.cralos.cleanarchitecture.business.interactors.notedetail

import com.cralos.cleanarchitecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.cralos.cleanarchitecture.business.data.cache.FORCE_UPDATE_NOTE_EXCEPTION
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.business.domain.model.NoteFactory
import com.cralos.cleanarchitecture.business.domain.state.DataState
import com.cralos.cleanarchitecture.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_FAILED
import com.cralos.cleanarchitecture.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_SUCCESS
import com.cralos.cleanarchitecture.di.DependencyContainer
import com.cralos.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailStateEvent
import com.cralos.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*


/*
Test cases:
1. updateNote_success_confirmNetworkAndCacheUpdated()
    a) select a random note from the cache
    b) update that note
    c) confirm UPDATE_NOTE_SUCCESS msg is emitted from flow
    d) confirm note is updated in network
    e) confirm note is updated in cache
2. updateNote_fail_confirmNetworkAndCacheUnchanged()
    a) attempt to update a note, fail since does not exist
    b) check for failure message from flow emission
    c) confirm nothing was updated in the cache
3. throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    a) attempt to update a note, force an exception to throw
    b) check for failure message from flow emission
    c) confirm nothing was updated in the cache
 */
@InternalCoroutinesApi
class UpdateNoteTest {

    //system in test
    private val updateNote: UpdateNote

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

        updateNote = UpdateNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }

    @Test
    fun updateNote_success_confirmNetworkAndCacheUpdated() = runBlocking {
        val randomNote = noteCacheDataSource.searchNotes("", "", 1).get(0)

        val updatedNote = noteFactory.createSingleNote(
            id = randomNote.id,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )

        updateNote.updateNote(
            note = updatedNote,
            stateEvent = NoteDetailStateEvent.UpdateNoteEvent()
        ).collect(object : FlowCollector<DataState<NoteDetailViewState>?> {
            override suspend fun emit(value: DataState<NoteDetailViewState>?) {
                assertEquals(value?.stateMessage?.response?.message, UPDATE_NOTE_SUCCESS)
            }
        })

        //Confirm cache was updated
        val cacheNote = noteCacheDataSource.searchNoteById(updatedNote.id)
        assertTrue { cacheNote == updatedNote }

        //confirm that network was updated
        val networkNote = noteNetworkDataSource.searchNote(updatedNote)
        assertTrue { networkNote == updatedNote }
    }

    @Test
    fun updateNote_fail_confirmNetworkAndCacheUnchanged() = runBlocking {

        //create note does not exist in cache
        val noteToUpdate = Note(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            created_at = UUID.randomUUID().toString(),
            updated_at = UUID.randomUUID().toString()
        )

        updateNote.updateNote(
            note = noteToUpdate,
            stateEvent = NoteDetailStateEvent.UpdateNoteEvent()
        ).collect(object : FlowCollector<DataState<NoteDetailViewState>?> {
            override suspend fun emit(value: DataState<NoteDetailViewState>?) {
                assertEquals(value?.stateMessage?.response?.message, UPDATE_NOTE_FAILED)
            }
        })

        //confirm nothing updated in cache
        val cacheNote = noteCacheDataSource.searchNoteById(noteToUpdate.id)
        assertTrue { cacheNote == null }

        //confirm nothing updated in network
        val nerworkNote = noteNetworkDataSource.searchNote(noteToUpdate)
        assertTrue { cacheNote == null }
    }

    @Test
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged() = runBlocking {
        //create note does not exist in cache
        val noteToUpdate = Note(
            id = FORCE_UPDATE_NOTE_EXCEPTION,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            created_at = UUID.randomUUID().toString(),
            updated_at = UUID.randomUUID().toString()
        )

        updateNote.updateNote(
            note = noteToUpdate,
            stateEvent = NoteDetailStateEvent.UpdateNoteEvent()
        ).collect(object : FlowCollector<DataState<NoteDetailViewState>?> {
            override suspend fun emit(value: DataState<NoteDetailViewState>?) {
                assert(
                    value?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN) ?: false
                )
            }
        })

        //confirm nothing updated in cache
        val cacheNote = noteCacheDataSource.searchNoteById(noteToUpdate.id)
        assertTrue { cacheNote == null }

        //confirm nothing updated in network
        val nerworkNote = noteNetworkDataSource.searchNote(noteToUpdate)
        assertTrue { cacheNote == null }
    }

}

















