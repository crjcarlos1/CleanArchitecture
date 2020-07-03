package com.cralos.cleanarchitecture.business.interactors.common

import com.cralos.cleanarchitecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.cralos.cleanarchitecture.business.data.cache.FORCE_DELETE_NOTE_EXCEPTION
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.business.domain.model.NoteFactory
import com.cralos.cleanarchitecture.business.domain.state.DataState
import com.cralos.cleanarchitecture.business.interactors.common.DeleteNote.Companion.DELETE_NOTE_FAILTURE
import com.cralos.cleanarchitecture.business.interactors.common.DeleteNote.Companion.DELETE_NOTE_SUCCESS
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

/***
 * Test cases
 * 1. deleteNote_success_confirmNetworkUpdate()
 *      a) delete a note
 *      b) check for success message from flow emission
 *      d) cofirm note was delete  from "notes" node in network
 *      e) confirm note was added to "deletes" node in network
 * 2. deleteNote_fail_confirmNetworkUnchange()
 *      a) attempt to delete a note, fail since does not exist
 *      b) check for failture message from flow emission
 *      c) confirm network was not change
 * 3. throwException_checkGenericError_confirmNetworkUnchanged()
 *      a) attemp to delete a note, force an exception to throw
 *      b) check for failture message from flow emission
 *      c) confirm network was not change
 */
@InternalCoroutinesApi
class DeleteNoteTest {

    //system in test
    private val deleteNote: DeleteNote<NoteListViewState>

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

        deleteNote = DeleteNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }

    @Test
    fun deleteNote_success_confirmNetworkUpdate() = runBlocking {

        val noteToDelete =
            noteCacheDataSource.searchNotes(query = "", filterAndOrder = "", page = 1).get(0)

        deleteNote.deleteNote(
            noteToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteEvent(noteToDelete)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(value?.stateMessage?.response?.message, DELETE_NOTE_SUCCESS)
            }
        })

        //confirm was deleted from 'notes' node
        val wasNoteDeleted = !noteNetworkDataSource.getAllNotes().contains(noteToDelete)
        assertTrue { wasNoteDeleted }

        //confirm was inserted from 'deletes' node
        val wasDeletedNoteInserted = noteNetworkDataSource.getDeletedNote().contains(noteToDelete)
        assertTrue { wasDeletedNoteInserted }

    }

    @Test
    fun deleteNote_fail_confirmNetworkUnchange() = runBlocking {
        val noteToDelete = Note(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            created_at = UUID.randomUUID().toString(),
            updated_at = UUID.randomUUID().toString()
        )

        deleteNote.deleteNote(
            noteToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteEvent(noteToDelete)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(value?.stateMessage?.response?.message, DELETE_NOTE_FAILTURE)
            }
        })

        //confirm was not deleted from 'notes' node
        val notes = noteNetworkDataSource.getAllNotes()
        val numNotesInCache = noteCacheDataSource.getNumNotes()
        assertTrue { numNotesInCache == notes.size }

        //confirm was not inserted from 'deletes' node
        val wasDeletedNoteInserted = !noteNetworkDataSource.getDeletedNote().contains(noteToDelete)
        assertTrue { wasDeletedNoteInserted }
    }

    @Test
    fun throwException_checkGenericError_confirmNetworkUnchanged() = runBlocking {
        val noteToDelete = Note(
            id = FORCE_DELETE_NOTE_EXCEPTION,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            created_at = UUID.randomUUID().toString(),
            updated_at = UUID.randomUUID().toString()
        )

        deleteNote.deleteNote(
            noteToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteEvent(noteToDelete)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN) ?: false
                )
            }
        })

        //confirm was not deleted from 'notes' node
        val notes = noteNetworkDataSource.getAllNotes()
        val numNotesInCache = noteCacheDataSource.getNumNotes()
        assertTrue { numNotesInCache == notes.size }

        //confirm was not inserted from 'deletes' node
        val wasDeletedNoteInserted = !noteNetworkDataSource.getDeletedNote().contains(noteToDelete)
        assertTrue { wasDeletedNoteInserted }
    }

}