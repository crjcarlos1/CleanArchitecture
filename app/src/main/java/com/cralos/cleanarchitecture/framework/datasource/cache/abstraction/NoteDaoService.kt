package com.cralos.cleanarchitecture.framework.datasource.cache.abstraction

import com.cralos.cleanarchitecture.business.domain.model.Note
import com.cralos.cleanarchitecture.framework.datasource.cache.database.NOTE_PAGINATION_PAGE_SIZE

/**Esta clase es muy parecida a: NoteCacheDataSource*/
interface NoteDaoService {

    suspend fun insertNote(note: Note): Long

    suspend fun deleteNote(primaryKey: String): Int

    suspend fun deleteNotes(notes: List<Note>): Int

    suspend fun updateNote(
        primaryKey: String,
        newTitle: String,
        newBody: String?,
        timestamp : String?
    ): Int

    suspend fun searchNotes(): List<Note>

    suspend fun getAllNotes(): List<Note>

    /**estas 4 funciones se agregaron*/
    suspend fun searchNotesOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNoteById(primaryKey: String): Note?

    suspend fun getNumNotes(): Int

    //testing
    suspend fun insertNotes(notes: List<Note>): LongArray

    /**se agrego esta funcion*/
    suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Note>

}