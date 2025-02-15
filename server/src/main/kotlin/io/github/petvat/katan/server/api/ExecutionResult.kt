package io.github.petvat.katan.server.api

import io.github.petvat.katan.shared.protocol.ErrorCode

sealed interface ExecutionResult<out T> {

    data class Success<T>(val data: Map<Int, T>) : ExecutionResult<T>
    data class Failure(val code: ErrorCode, val description: String) : ExecutionResult<Nothing>
}
