package com.cralos.cleanarchitecture.business.interactors.notelist

import com.cralos.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.business.domain.state.*
import com.cralos.cleanarchitecture.business.util.safeApiCall
import com.cralos.cleanarchitecture.business.util.safeCacheCall
import com.cralos.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteMultipleNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    private var onDeleteError: Boolean = false

    fun deleteNotes(
        notes: List<Note>,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow {

        val successfulDeletes: ArrayList<Note> = ArrayList()

        for (note in notes) {

            val cacheResult = safeCacheCall(Dispatchers.IO) {
                noteCacheDataSource.deleteNote(note.id)
            }

            val response = object : CacheResponseHandler<NoteListViewState, Int>(
                response = cacheResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObject: Int): DataState<NoteListViewState>? {
                    if (resultObject < 0) {//error
                        onDeleteError = true
                    } else {
                        successfulDeletes.add(note)
                    }
                    return null
                }
            }.getResult()

            if (response?.stateMessage?.response?.message?.contains(stateEvent.errorInfo()) == true) {
                onDeleteError = true
            }

        }

        if (onDeleteError) {
            emit(
                DataState.data<NoteListViewState>(
                    response = Response(
                        message = DELETE_NOTES_ERRORS,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Success()
                    ),
                    data = null, stateEvent = stateEvent
                )
            )
        } else {
            emit(
                DataState.data<NoteListViewState>(
                    response = Response(
                        message = DELETE_NOTES_SUCCESS,
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Success()
                    ),
                    data = null, stateEvent = stateEvent
                )
            )
        }

        updateNetwork(successfulDeletes)

    }

    private suspend fun updateNetwork(successfulDeletes: ArrayList<Note>) {
        for (note in successfulDeletes) {
            //delete from 'notes' node
            safeApiCall(Dispatchers.IO) {
                noteNetworkDataSource.deleteNote(note.id)
            }
            //insert into 'deletes' node
            safeApiCall(Dispatchers.IO) {
                noteNetworkDataSource.insertDeletedNote(note)
            }
        }
    }

    companion object {
        val DELETE_NOTES_SUCCESS = "Successfully deleted notes."
        val DELETE_NOTES_ERRORS =
            "Not all the notes you selected were deleted. There was some errors."
        val DELETE_NOTES_YOU_MUST_SELECT = "You haven't selected any notes to delete."
        val DELETE_NOTES_ARE_YOU_SURE = "Are you sure you want to delete these?"
    }

}