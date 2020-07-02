package com.cralos.cleanarchitecture.framework.datasource.network.abstraction

import com.cralos.cleanarchitecture.business.domain.model.Note

/**tiene lo mismo que la clase: NoteNetworkDataSource*/
interface NoteFirestoreService {

    suspend fun insertOrUpdateNote(note: Note)

    suspend fun deleteNote(primaryKey: String)

    suspend fun insertDeletedNote(note: Note)

    suspend fun insertDeletedNotes(notes: List<Note>)

    suspend fun deleteDeletedNote(note: Note)

    suspend fun getDeletedNote(): List<Note>

    suspend fun deleteAllNotes()

    suspend fun searchNote(note: Note): Note?

    suspend fun getAllNotes(): List<Note>

    suspend fun insertOrUpdateNotes(notes: List<Note>)

}