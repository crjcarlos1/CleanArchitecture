package com.cralos.cleanarchitecture.business.interactors.notelist

import com.cralos.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.business.domain.state.*
import com.cralos.cleanarchitecture.business.util.safeCacheCall
import com.cralos.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchNotes(
    private val noteCacheDataSource: NoteCacheDataSource
) {

    fun searchNotes(
        query: String,
        filterAndOrder: String,
        page: Int,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow {
        var updatedPage = page

        if (page <= 0) {
            updatedPage = 1
        }

        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteCacheDataSource.searchNotes(
                query = query,
                filterAndOrder = filterAndOrder,
                page = updatedPage
            )
        }

        val response = object : CacheResponseHandler<NoteListViewState, List<Note>>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override fun handleSuccess(resultObject: List<Note>): DataState<NoteListViewState> {

                var message: String? = SEARCH_NOTES_SUCCESS
                var uiComponentType: UIComponentType = UIComponentType.None()

                if (resultObject.size == 0) {
                    message = SEARCH_NOTES_NO_MATCHING_RESULTS
                    uiComponentType = UIComponentType.Toast()
                }

                return DataState.data(
                    response = Response(
                        message = message,
                        uiComponentType = uiComponentType,
                        messageType = MessageType.Success()
                    ),
                    data = NoteListViewState(noteList = ArrayList(resultObject)),
                    stateEvent = stateEvent
                )

            }
        }.getResult()

        emit(response)

    }


    companion object {
        val SEARCH_NOTES_SUCCESS = "Successfully retrived list of notes"
        val SEARCH_NOTES_NO_MATCHING_RESULTS = "There are no notes  that mach that query"
        val SEARCH_NOTES_FAILED = "Failed to retrived the list of notes"
    }

}