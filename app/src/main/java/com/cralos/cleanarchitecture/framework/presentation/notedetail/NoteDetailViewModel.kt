package com.cralos.cleanarchitecture.framework.presentation.notedetail

import com.cralos.cleanarchitecture.business.domain.state.StateEvent
import com.cralos.cleanarchitecture.business.interactors.notedetail.NoteDetailInteractors
import com.cralos.cleanarchitecture.framework.presentation.common.BaseViewModel
import com.cralos.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class NoteDetailViewModel
@Inject
constructor(
    private val noteDetailInteractors: NoteDetailInteractors
): BaseViewModel<NoteDetailViewState>(){

    override fun handleNewData(data: NoteDetailViewState) {

    }

    override fun setStateEvent(stateEvent: StateEvent) {

    }

    override fun initNewViewState(): NoteDetailViewState {
        return NoteDetailViewState()
    }

}














