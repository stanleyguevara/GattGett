package com.stanleyguevara.gattgett.utils

import android.util.Log
import androidx.lifecycle.*

class ForegroundDetector : LiveData<Boolean>(), LifecycleObserver {

    private val TAG: String = ForegroundDetector::class.java.simpleName

    init {
        // this observer is meant to exist until process goes away, so no removing
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        Log.d(TAG, "Returning to foreground...")
        value = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        value = false
        Log.d(TAG, "Moving to background...")
    }

}