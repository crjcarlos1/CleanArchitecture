package com.cralos.cleanarchitecture.business.interactors.notelist

import com.cralos.cleanarchitecture.business.data.cache.CacheErrors
import com.cralos.cleanarchitecture.business.data.cache.FORCE_GENERAL_FAILURE
import com.cralos.cleanarchitecture.business.data.cache.FORCE_NEW_NOTE_EXCEPTION
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.NoteFactory
import com.cralos.cleanarchitecture.business.domain.state.DataState
import com.cralos.cleanarchitecture.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_SUCCESS
import com.cralos.cleanarchitecture.di.DependencyContainer
import com.cralos.cleanarchitecture.framework.presentation.notelist.state.NoteListStateEvent
import com.cralos.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

/**
 *
 * Test cases
 * 1. insert_new_note_success_confirmNetworkAndCacheUpdated()
 *      a)  insert a new note
 *      b)  listen for INSERT_NOTE_SUCCESS emission from flow
 *      c)  confirm cache was updated with new note
 *      d)  confirm network was updated with new note
 * 2.insertNote_fail_confirmNetworkAndCacheUnchange()
 *      a)  insert a new note
 *      b)  force a failture (return -1 from db operation)
 *      c)  listen for INSERT_NOTE_FAILED emission from flow
 *      d)  confirm cache was not updated
 *      e)  confirm network was not updated
 * 3.throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
 *      a)  insert a new note
 *      b)  force an exception
 *      c)  listen for CACHE_ERROR_UNKNOWN emission from flow
 *      d)  confirm cache was not updated
 *      e)  confirm network was not updated
 *
 */
@InternalCoroutinesApi
class InsertNewNoteTest {

    //system in test
    private val insertNewNote: InsertNewNote

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

        insertNewNote = InsertNewNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource,
            noteFactory = noteFactory
        )
    }

    /**
     * 1. insert_new_note_success_confirmNetworkAndCacheUpdated()
     *      a)  insert a new note
     *      b)  listen for INSERT_NOTE_SUCCESS emission from flow
     *      c)  confirm cache was updated with new note
     *      d)  confirm network was updated with new note
     */
    @Test
            /**Solo cuando se trabaja con test es bueno utilizar runBlocking*/
    fun insert_new_note_success_confirmNetworkAndCacheUpdated() = runBlocking {

        val newNote = noteFactory.createSingleNote(
            id = null,
            title = UUID.randomUUID().toString()
        )

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(
                title = newNote.title,
                body = newNote.body
            )
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    INSERT_NOTE_SUCCESS
                )
            }
        })

        //confirm cache was updated
        val cacheNoteThatWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue { cacheNoteThatWasInserted == newNote }

        //confirm network was updated
        val networkNoteThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertTrue { networkNoteThatWasInserted == newNote }

    }

    /**
     * 2.insertNote_fail_confirmNetworkAndCacheUnchange()
     *      a)  insert a new note
     *      b)  force a failture (return -1 from db operation)
     *      c)  listen for INSERT_NOTE_FAILED emission from flow
     *      d)  confirm cache was not updated
     *      e)  confirm network was not updated
     */
    @Test
    fun insertNote_fail_confirmNetworkAndCacheUnchange() = runBlocking {
        val newNote = noteFactory.createSingleNote(
            id = FORCE_GENERAL_FAILURE,
            title = UUID.randomUUID().toString()
        )

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(
                title = newNote.title,
                body = newNote.body
            )
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    InsertNewNote.INSERT_NOTE_FAILED
                )
            }
        })

        //confirm cache was NOT updated
        val cacheNoteThatWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue { cacheNoteThatWasInserted == null }

        //confirm network was NOT updated
        val networkNoteThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertTrue { networkNoteThatWasInserted == null }
    }

    /**
     * 3.throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
     *      a)  insert a new note
     *      b)  force an exception
     *      c)  listen for CACHE_ERROR_UNKNOWN emission from flow
     *      d)  confirm cache was not updated
     *      e)  confirm network was not updated
     */
    @Test
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged() = runBlocking {
        val newNote = noteFactory.createSingleNote(
            id = FORCE_NEW_NOTE_EXCEPTION,
            title = UUID.randomUUID().toString()
        )

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(
                title = newNote.title,
                body = newNote.body
            )
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.response?.message?.contains(CacheErrors.CACHE_ERROR_UNKNOWN)
                        ?: false
                )
            }
        })

        //confirm cache was NOT updated
        val cacheNoteThatWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue { cacheNoteThatWasInserted == null }

        //confirm network was NOT updated
        val networkNoteThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertTrue { networkNoteThatWasInserted == null }
    }

}



























