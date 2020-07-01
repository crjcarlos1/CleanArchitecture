package com.cralos.cleanarchitecture.framework.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cralos.cleanarchitecture.R

class MainActivity : AppCompatActivity() {
    private val TAG = "AppDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}