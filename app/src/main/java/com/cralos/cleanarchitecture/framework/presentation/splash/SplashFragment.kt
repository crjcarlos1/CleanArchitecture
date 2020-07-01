package com.cralos.cleanarchitecture.framework.presentation.splash

import android.os.Bundle
import android.view.View
import com.cralos.cleanarchitecture.R
import com.cralos.cleanarchitecture.framework.presentation.common.BaseNoteFragment

class SplashFragment : BaseNoteFragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun inject() {
        TODO("prepare dagger")
    }

}
