package com.cralos.cleanarchitecture.framework.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cralos.cleanarchitecture.R
import com.cralos.cleanarchitecture.business.domain.state.DialogInputCaptureCallback
import com.cralos.cleanarchitecture.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.EMAIL
import com.cralos.cleanarchitecture.framework.presentation.common.BaseNoteFragment
import com.cralos.cleanarchitecture.util.printLogD
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class SplashFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseNoteFragment(R.layout.fragment_splash) {

    val viewModel: SplashViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkFirebaseAuth()
    }

    private fun checkFirebaseAuth() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            displayCapturePassword()
        } else {
            subscribeObservers()
        }
    }

    // add password input b/c someone used my firestore and deleted the data
    private fun displayCapturePassword() {
        uiController.displayInputCaptureDialog(
            "Enter a title",
            object : DialogInputCaptureCallback {
                override fun onTextCaptured(text: String) {
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(EMAIL, text)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                printLogD(
                                    "MainActivity",
                                    "Signing in to Firebase: ${it.result}"
                                )
                                subscribeObservers()
                            }
                        }
                }
            }
        )
    }

    private fun subscribeObservers() {
        viewModel.hasSyncBeenExecuted()
            .observe(viewLifecycleOwner, Observer { hasSyncBeenExecuted ->

                if (hasSyncBeenExecuted) {
                    navNoteListFragment()
                }
            })
    }

    private fun navNoteListFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_noteListFragment)
    }

    override fun inject() {
        //getAppComponent().inject(this)
    }

}





































