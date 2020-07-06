package com.cralos.cleanarchitecture.framework.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cralos.cleanarchitecture.R
import com.cralos.cleanarchitecture.util.printLogD
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    val TAG = "AppDebug"

    @Inject
    lateinit var firebaseAuth : FirebaseAuth

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        printLogD("MainActivity","firebaseAuth: $firebaseAuth")
    }

}