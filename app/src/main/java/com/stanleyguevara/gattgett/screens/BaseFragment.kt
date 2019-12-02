package com.stanleyguevara.gattgett.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    abstract fun getTitle(): String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.title = getTitle()
    }
}