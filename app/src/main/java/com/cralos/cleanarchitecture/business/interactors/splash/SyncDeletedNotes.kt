package com.cralos.cleanarchitecture.business.interactors.splash

import com.cralos.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.ApiResponseHandler
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.business.domain.state.DataState
import com.cralos.cleanarchitecture.business.util.safeApiCall
import com.cralos.cleanarchitecture.business.util.safeCacheCall
import com.cralos.cleanarchitecture.util.printLogD
import kotlinx.coroutines.Dispatchers

class SyncDeletedNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    suspend fun syncDeletedNotes() {
        val apiResult = safeApiCall(Dispatchers.IO) {
            noteNetworkDataSource.getDeletedNote()
        }

        val response = object : ApiResponseHandler<List<Note>, List<Note>?>(
            response = apiResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: List<Note>?): DataState<List<Note>> {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }
        }.getResult()

        val notes = response?.data ?: ArrayList()

        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteCacheDataSource.deleteNotes(notes)
        }

        object : CacheResponseHandler<Int, Int>(
            response = cacheResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: Int): DataState<Int>? {
                printLogD("SyncNotes", "Num deleted notes: $resultObj")
                return DataState.data(
                    response = null,
                    data = null, stateEvent = null
                )
            }
        }

    }

}