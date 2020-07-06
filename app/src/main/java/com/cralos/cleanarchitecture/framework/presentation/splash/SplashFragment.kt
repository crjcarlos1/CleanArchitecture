package com.cralos.cleanarchitecture.framework.presentation.splash

import android.os.Bundle
import android.view.View
import com.cralos.cleanarchitecture.R
import com.cralos.cleanarchitecture.framework.presentation.common.BaseNoteFragment
import com.cralos.cleanarchitecture.util.cLog
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SplashFragment : BaseNoteFragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseCrashlytics.getInstance().log("THIS IS THE FIRST LOG!")
    }


    override fun inject() {
        TODO("prepare dagger")
    }

}
