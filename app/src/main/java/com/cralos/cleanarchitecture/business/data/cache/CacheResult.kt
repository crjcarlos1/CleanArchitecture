package com.cralos.cleanarchitecture.business.data.cache

/**wrapper de los resultados obtenidos de la database*/
sealed class CacheResult<out T> {

    data class Success<out T>(val value: T) : CacheResult<T>()

    data class GenericError(val errorMessage: String? = null) : CacheResult<Nothing>()

}