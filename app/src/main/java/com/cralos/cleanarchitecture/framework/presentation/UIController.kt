package com.cralos.cleanarchitecture.framework.presentation

import com.cralos.cleanarchitecture.business.domain.state.DialogInputCaptureCallback
import com.cralos.cleanarchitecture.business.domain.state.Response
import com.cralos.cleanarchitecture.business.domain.state.StateMessageCallback

interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback)

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

}