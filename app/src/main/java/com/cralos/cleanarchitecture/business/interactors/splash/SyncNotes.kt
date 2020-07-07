package com.cralos.cleanarchitecture.business.interactors.splash

import com.cralos.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.ApiResponseHandler
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.business.domain.state.DataState
import com.cralos.cleanarchitecture.business.util.safeApiCall
import com.cralos.cleanarchitecture.business.util.safeCacheCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    suspend fun syncNotes() {
        val cachedNoteList = getCachedNotes()
        val networkNotesList = getNetworkNotes()
        syncNetworkNotesWithCachedNotes(ArrayList(cachedNoteList), networkNotesList)
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

    private suspend fun getNetworkNotes(): List<Note> {
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
        return response?.data ?: ArrayList()
    }

    //get all notes from network
    //if they do not exist in cache, insert them
    //if they do exist in ccahe, make sure they are up  to date
    //while looping, remove notes from the cachedNotes List. If any remain, it means they
    //should be in the network but aren't. So insert them
    private suspend fun syncNetworkNotesWithCachedNotes(
        cachedNotes: ArrayList<Note>,
        networkNotes: List<Note>
    ) =
        withContext(Dispatchers.IO) {

            for (note in networkNotes) {
                noteCacheDataSource.searchNoteById(note.id)?.let { cachedNote ->
                    cachedNotes.remove(cachedNote)
                    checkIfCachedNoteRequieresUpdate(cachedNote, note)
                } ?: noteCacheDataSource.insertNote(note)
            }

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
                    networkNote.id,
                    networkNote.title,
                    networkNote.body,
                    networkNote.updated_at
                )
            }
        } else if (networkUpdatedAt<cacheUpdatedAt){
            safeApiCall(Dispatchers.IO) {
                noteNetworkDataSource.insertOrUpdateNote(cachedNote)
            }
        }


    }

}

















