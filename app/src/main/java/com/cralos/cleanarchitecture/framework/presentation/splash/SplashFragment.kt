package com.cralos.cleanarchitecture.framework.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.cralos.cleanarchitecture.R
import com.cralos.cleanarchitecture.framework.presentation.common.BaseNoteFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SplashFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseNoteFragment(R.layout.fragment_splash) {

    val viewModel: SplashViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseCrashlytics.getInstance().log("THIS IS THE FIRST LOG!")
    }


    override fun inject() {
        TODO("prepare dagger")
    }

}
