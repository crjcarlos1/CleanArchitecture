package com.cralos.cleanarchitecture.business.data

import com.cralos.cleanarchitecture.business.domain.model.Note
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NoteDataFactory(
    private val testClassLoader: ClassLoader
) {

    fun produceListOfNotes(): List<Note> {
        val notes: List<Note> = Gson().fromJson(
            getNotesFromFile("note_list.json"),
            object : TypeToken<List<Note>>() {}.type
        )
        return notes
    }

    fun produceHashMapOfNotes(noteList: List<Note>): HashMap<String, Note> {
        val map = HashMap<String, Note>()
        for (note in noteList) {
            map.put(note.id, note)
        }
        return map
    }

    fun produceEmptyListOfMotes(): List<Note>{
        return ArrayList()
    }

    fun getNotesFromFile(fileName: String): String {
        return testClassLoader.getResource(fileName).readText()
    }

}