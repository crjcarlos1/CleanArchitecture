package com.cralos.cleanarchitecture.di

import com.cralos.cleanarchitecture.business.data.cache.FakeNoteCacheDataSourceImpl
import com.cralos.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.cralos.cleanarchitecture.business.data.network.FakeNoteNetworkDataSourceImpl
import com.cralos.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.cralos.cleanarchitecture.business.domain.model.NoteFactory
import com.cralos.cleanarchitecture.business.domain.util.DateUtil
import com.cralos.cleanarchitecture.util.isUnitTest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DependencyContainer {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
    val dateUtil = DateUtil(dateFormat)
    lateinit var noteNetworkDataSource: NoteNetworkDataSource
    lateinit var noteCacheDataSource: NoteCacheDataSource
    lateinit var noteFactory: NoteFactory

    init {
        isUnitTest = true // for Logger.kt
    }

    fun build() {
        noteFactory = NoteFactory(dateUtil)
        noteNetworkDataSource = FakeNoteNetworkDataSourceImpl(
            notesData = HashMap(),
            deletedNotesData = HashMap()
        )
        noteCacheDataSource = FakeNoteCacheDataSourceImpl(
            notesData = HashMap(),
            dateUtil = dateUtil
        )
    }

}