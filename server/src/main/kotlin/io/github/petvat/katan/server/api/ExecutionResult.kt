package io.github.petvat.katan.server.api

sealed interface ExecutionResult<out T> {
    val description: String

    data class Success<T>(val data: Map<Int, T>, override val description: String) : ExecutionResult<T>
    data class Failure(override val description: String) : ExecutionResult<Nothing>
}
