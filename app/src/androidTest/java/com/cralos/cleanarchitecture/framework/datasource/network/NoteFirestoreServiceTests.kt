package com.cralos.cleanarchitecture.framework.datasource.network

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cralos.cleanarchitecture.di.TestAppComponent
import com.cralos.cleanarchitecture.framework.BaseTest
import com.cralos.cleanarchitecture.framework.datasource.data.NoteDataFactory
import com.cralos.cleanarchitecture.framework.datasource.network.abstraction.NoteFirestoreService
import com.cralos.cleanarchitecture.framework.datasource.network.implementation.NoteFirestoreServiceImpl
import com.cralos.cleanarchitecture.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.NOTES_COLLECTION
import com.cralos.cleanarchitecture.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.USER_ID
import com.cralos.cleanarchitecture.framework.datasource.network.mappers.NetworkMapper
import com.cralos.cleanarchitecture.util.printLogD
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteFirestoreServiceTests : BaseTest() {

    // system in test
    private lateinit var noteFirestoreService: NoteFirestoreService

    // dependencies
    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var noteDataFactory: NoteDataFactory

    @Inject
    lateinit var networkMapper: NetworkMapper

    init {
        injectTest()
        signIn()
        insertTestData()
    }

    @Before
    fun before() {
        noteFirestoreService = NoteFirestoreServiceImpl(
            firebaseAuth = FirebaseAuth.getInstance(),
            firestore = firestore,
            networkMapper = networkMapper
        )
    }

    private fun signIn() = runBlocking {
        firebaseAuth.signInWithEmailAndPassword(
            EMAIL,
            PASSWORD
        ).await()
    }

    private fun insertTestData() {
        val entityList = networkMapper.noteListToEntityList(noteDataFactory.produceListOfNotes())
        for (entity in entityList){
            firestore.collection(NOTES_COLLECTION).document(USER_ID).collection(NOTES_COLLECTION).document(entity.id).set(entity)
        }
    }

    @Test
    fun a_insertSingleNote_CBS() = runBlocking {
        val note = noteDataFactory.createSingleNote(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )

        noteFirestoreService.insertOrUpdateNote(note)

        val searchResult = noteFirestoreService.searchNote(note)

        assertEquals(note, searchResult)
    }

    @Test
    fun b_queryAllNotes() = runBlocking {
        val notes = noteFirestoreService.getAllNotes()
        printLogD("NoteFirestoreService","notes: ${notes.size}")
        assertTrue { notes.size == 11 }
    }

    companion object {
        const val EMAIL = "carlos"
        const val PASSWORD = "123"
    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent).inject(this)
    }
}







