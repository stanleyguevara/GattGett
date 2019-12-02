package com.stanleyguevara.gattgett.screens.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.stanleyguevara.gattgett.utils.SingleEvent
import org.koin.core.KoinComponent
import org.koin.core.inject

class GoogleLoginHelper : KoinComponent {

    private val context: Context by inject()
    private val client: GoogleSignInClient
    private val _account = MutableLiveData<SingleEvent<GoogleSignInAccount?>>()
    val account: LiveData<SingleEvent<GoogleSignInAccount?>> = _account

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        client = GoogleSignIn.getClient(context, gso)
        eval()
    }

    fun getSignInIntent(): Intent {
        return client.signInIntent
    }

    // Happy path. Real world solution would require better error handling
    fun handleSignInIntent(intent: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                _account.value = SingleEvent(account)
            } else {
                _account.value = SingleEvent(null)
            }
        } catch (e: ApiException) {
            _account.value = SingleEvent(null)
        }
    }

    fun logout() {
        client.signOut()
        eval()
    }

    private fun eval() {
        _account.value = SingleEvent(GoogleSignIn.getLastSignedInAccount(context))
    }

}