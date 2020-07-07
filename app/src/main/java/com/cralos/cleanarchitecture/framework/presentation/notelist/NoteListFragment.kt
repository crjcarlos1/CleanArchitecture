package com.cralos.cleanarchitecture.framework.presentation.notelist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.cralos.cleanarchitecture.R
import com.cralos.cleanarchitecture.business.domain.util.DateUtil
import com.cralos.cleanarchitecture.framework.presentation.common.BaseNoteFragment
import com.cralos.cleanarchitecture.framework.presentation.common.NoteViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


const val NOTE_LIST_STATE_BUNDLE_KEY = "com.codingwithmitch.cleannotes.notes.framework.presentation.notelist.state"

@FlowPreview
@ExperimentalCoroutinesApi
class NoteListFragment
    constructor(
        private val viewModelFactory: ViewModelProvider.Factory,
        private val dateUtil: DateUtil
    ): BaseNoteFragment(R.layout.fragment_note_list)
{

    val viewModel : NoteListViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
        TODO("prepare dagger")
    }

}

