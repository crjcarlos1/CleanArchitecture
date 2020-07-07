package com.cralos.cleanarchitecture.business

import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cralos.cleanarchitecture.di.TestAppComponent
import com.cralos.cleanarchitecture.framework.presentation.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
class TemTest{

    val application : TestBaseApplication= ApplicationProvider.getApplicationContext() as TestBaseApplication

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    init {
        (application.appComponent as TestAppComponent).inject(this)
    }

    @Test
    fun someRandomTest(){
        assert(::firebaseFirestore.isInitialized)
    }


}