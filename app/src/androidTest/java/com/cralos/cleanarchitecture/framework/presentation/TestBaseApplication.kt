package com.cralos.cleanarchitecture.framework.presentation

import com.cralos.cleanarchitecture.di.DaggerTestAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TestBaseApplication : BaseApplication() {

    override fun initAppComponent() {
        super.initAppComponent()
        //appComponent = DaggerTestAppComponent.factory().create(this)
        appComponent= DaggerTestAppComponent.factory().create(this)
    }

}