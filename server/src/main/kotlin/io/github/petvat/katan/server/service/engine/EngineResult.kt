package io.github.petvat.katan.server.service.engine

sealed interface EngineResult<out T> {
    data class Success<T>(val value: T) : EngineResult<T>
    data class Failure(val description: String) : EngineResult<Nothing>

    companion object {
        fun <T> of(value: T) = Success(value)
    }
}

