package com.stanleyguevara.gattgett.utils

open class SingleEvent<out T>(private val content: T) {

    var consumed = false
        private set // Allow external read but not write

    fun consume(): T? {
        return if (consumed) {
            null
        } else {
            consumed = true
            content
        }
    }

    fun peek(): T = content
}