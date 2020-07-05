package com.cralos.cleanarchitecture.business.interactors.splash

import com.cralos.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.ApiResponseHandler
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.business.domain.state.DataState
import com.cralos.cleanarchitecture.business.domain.util.DateUtil
import com.cralos.cleanarchitecture.business.util.safeApiCall
import com.cralos.cleanarchitecture.business.util.safeCacheCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SyncNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
    private val dateUtil: DateUtil
) {

    suspend fun syncNotes() {
        val cachedNoteList = getCachedNotes()
        syncNetworkNotesWithCachedNotes(ArrayList(cachedNoteList))
    }

    private suspend fun getCachedNotes(): List<Note> {

        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteCacheDataSource.getAllNotes()
        }

        val response = object : CacheResponseHandler<List<Note>, List<Note>?>(
            response = cacheResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: List<Note>?): DataState<List<Note>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }
        }.getResult()

        return response?.data ?: ArrayList()

    }

    //get all notes from network
    //if they do not exist in cache, insert them
    //if they do exist in ccahe, make sure they are up  to date
    //while looping, remove notes from the cachedNotes List. If any remain, it means they
    //should be in the network but aren't. So insert them
    private suspend fun syncNetworkNotesWithCachedNotes(cachedNotes: ArrayList<Note>) =
        withContext(Dispatchers.IO) {

            val networkResult = safeApiCall(Dispatchers.IO) {
                noteNetworkDataSource.getAllNotes()
            }

            val response = object : ApiResponseHandler<List<Note>, List<Note>?>(
                response = networkResult,
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

            val noteList = response?.data ?: ArrayList()

            val job = launch {
                for (note in noteList) {
                    noteCacheDataSource.searchNoteById(note.id)?.let { cachedNote ->
                        cachedNotes.remove(cachedNote)
                        checkIfCachedNoteRequieresUpdate(cachedNote, note)
                    } ?: noteCacheDataSource.insertNote(note)
                }
            }
            job.join()//wait

            //insert remaining into network
            for (cachedNote in cachedNotes) {
                safeApiCall(Dispatchers.IO) {
                    noteNetworkDataSource.insertOrUpdateNote(cachedNote)
                }
            }

        }

    private suspend fun checkIfCachedNoteRequieresUpdate(cachedNote: Note, networkNote: Note) {
        val cacheUpdatedAt = cachedNote.updated_at
        val networkUpdatedAt = networkNote.updated_at

        if (networkUpdatedAt > cacheUpdatedAt) {
            safeCacheCall(Dispatchers.IO) {
                noteCacheDataSource.updateNote(
                    primaryKey = networkNote.id,
                    newTitle = networkNote.title,
                    newBody = networkNote.body
                )
            }
        } else {
            safeApiCall(Dispatchers.IO) {
                noteNetworkDataSource.insertOrUpdateNote(cachedNote)
            }
        }


    }

}

















