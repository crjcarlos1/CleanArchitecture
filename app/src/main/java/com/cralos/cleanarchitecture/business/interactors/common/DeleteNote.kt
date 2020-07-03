package com.cralos.cleanarchitecture.business.interactors.common

import com.cralos.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.business.domain.state.*
import com.cralos.cleanarchitecture.business.util.safeApiCall
import com.cralos.cleanarchitecture.business.util.safeCacheCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**Generic viewState*/
class DeleteNote<ViewState>(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    fun deleteNote(
        note: Note,
        stateEvent: StateEvent
    ): Flow<DataState<ViewState>?> = flow {

        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteCacheDataSource.deleteNote(note.id)
        }

        val response = object : CacheResponseHandler<ViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override fun handleSuccess(resultObject: Int): DataState<ViewState> {
                return if (resultObject > 0) {
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Success()
                        ), data = null, stateEvent = stateEvent
                    )
                } else {
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_FAILTURE,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        ), data = null, stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(response)
        updateNetwork(response?.stateMessage?.response?.message, note)

    }

    private suspend fun updateNetwork(message: String?, note: Note) {
        if (message.equals(DELETE_NOTE_SUCCESS)) {
            //delete from  'notes' node
            safeApiCall(Dispatchers.IO) {
                noteNetworkDataSource.deleteNote(note.id)
            }

            //insert into 'deletes' node
            safeApiCall(Dispatchers.IO) {
                noteNetworkDataSource.insertDeletedNote(note)
            }
        } else {
        }
    }

    companion object {
        val DELETE_NOTE_SUCCESS = "Successfully delete the note."
        val DELETE_NOTE_FAILTURE = "Failture to delete note"
    }

}