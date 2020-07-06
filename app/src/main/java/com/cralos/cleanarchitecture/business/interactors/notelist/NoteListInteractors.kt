package com.cralos.cleanarchitecture.business.interactors.notelist

import com.cralos.cleanarchitecture.business.interactors.common.DeleteNote
import com.cralos.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState

class NoteListInteractors (
    val insertNewNote: InsertNewNote,
    val deleteNote: DeleteNote<NoteListViewState>,
    val searchNotes: SearchNotes,
    val getNumNotes: GetNumNotes,
    val restoreDeletedNote: RestoreDeletedNote,
    val deletedMultipleNotes: DeleteMultipleNotes
)