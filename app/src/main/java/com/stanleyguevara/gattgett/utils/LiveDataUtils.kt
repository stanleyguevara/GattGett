package com.stanleyguevara.gattgett.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <A, B, R> zip(a: LiveData<A>, b: LiveData<B>, zipper: (A, B) -> R): LiveData<R> {

    return MediatorLiveData<R>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null) {
                value = zipper(localLastA, localLastB)
            }
        }

        addSource(a) {
            lastA = it
            update()
        }

        addSource(b) {
            lastB = it
            update()
        }
    }
}