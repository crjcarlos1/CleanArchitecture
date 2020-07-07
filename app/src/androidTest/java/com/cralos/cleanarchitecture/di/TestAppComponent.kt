package com.cralos.cleanarchitecture.di

import com.cralos.cleanarchitecture.business.TemTest
import com.cralos.cleanarchitecture.framework.presentation.TestBaseApplication
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@Component(
    modules = [
        AppModule::class,
        TestModule::class]
)
interface TestAppComponent :AppComponent{

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance app: TestBaseApplication) : TestAppComponent
    }

    fun inject(tempTest: TemTest)

}