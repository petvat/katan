package io.github.petvat.katan.client


interface ResponseListener<T> {
    fun responseReceived(response: T)
}

