package com.cralos.cleanarchitecture.framework.presentation.notelist

import android.content.SharedPreferences
import com.cralos.cleanarchitecture.business.domain.model.NoteFactory
import com.cralos.cleanarchitecture.business.domain.state.StateEvent
import com.cralos.cleanarchitecture.business.interactors.notelist.NoteListInteractors
import com.cralos.cleanarchitecture.framework.presentation.common.BaseViewModel
import com.cralos.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class NoteListViewModel
constructor(
    private val noteListInteractors: NoteListInteractors,
    private val noteFactory: NoteFactory,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
): BaseViewModel<NoteListViewState>(){

    override fun handleNewData(data: NoteListViewState) {

    }

    override fun setStateEvent(stateEvent: StateEvent) {
    }

    override fun initNewViewState(): NoteListViewState {
        return NoteListViewState()
    }

}