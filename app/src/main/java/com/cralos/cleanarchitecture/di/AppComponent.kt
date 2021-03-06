package com.cralos.cleanarchitecture.di

import com.cralos.cleanarchitecture.framework.presentation.BaseApplication
import com.cralos.cleanarchitecture.framework.presentation.MainActivity
import com.cralos.cleanarchitecture.framework.presentation.notedetail.NoteDetailFragment
import com.cralos.cleanarchitecture.framework.presentation.notelist.NoteListFragment
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
@Component(
    modules = [
        AppModule::class,
        ProductionModule::class,
        NoteViewModelModule::class,
    NoteFragmentFactoryModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: BaseApplication): AppComponent
    }

    fun inject(mainActivity: MainActivity)

    fun inject(noteListFragment: NoteListFragment)

    fun inject(noteDetailFragment: NoteDetailFragment)

}