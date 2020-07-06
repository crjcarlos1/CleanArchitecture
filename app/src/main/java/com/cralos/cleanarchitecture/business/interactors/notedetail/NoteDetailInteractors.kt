package com.cralos.cleanarchitecture.business.interactors.notedetail

import com.cralos.cleanarchitecture.business.interactors.common.DeleteNote
import com.cralos.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailViewState

class NoteDetailInteractors (
    val deleteNote: DeleteNote<NoteDetailViewState>,
    val updateNote: UpdateNote
)