package com.cralos.cleanarchitecture.business.data.cache


import com.cralos.cleanarchitecture.business.domain.state.*

/**Clase que toma el resultado de CacheResult y transformarlo a dataState*/
abstract class CacheResponseHandler<ViewState, Data>(
    private val response: CacheResult<Data?>,
    private val stateEvent: StateEvent?
) {

    suspend fun getResult(): DataState<ViewState> {
        return when (response) {

            is CacheResult.GenericError -> {
                DataState.error(
                    response = Response(
                        message = "${stateEvent?.errorInfo()} \n\nReason: ${response.errorMessage}",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )
            }

            is CacheResult.Success -> {
                if (response.value == null) {
                    DataState.error(
                        response = Response(
                            message = "${stateEvent?.errorInfo()} \n\nReason: ${CacheErrors.CACHE_DATA_NULL}",
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        ),
                        stateEvent = stateEvent
                    )
                } else {
                    handleSuccess(resultObject = response.value)
                }
            }

        }
    }

    abstract fun handleSuccess(resultObject: Data): DataState<ViewState>

}