package com.iti.presentation.screens.auth

import android.content.Context
import androidx.activity.compose.LocalActivity
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.iti.presentation.R

class FacebookSignInHelper(
    private val context: Context,
    private val activityResultRegistryOwner: ActivityResultRegistryOwner?,
    private val callbackManager: CallbackManager,
    private val loginManager: LoginManager,
    private val onError: (message: String) -> Unit
) {

    fun signIn() {
        val registryOwner = activityResultRegistryOwner
        if (registryOwner != null) {
            loginManager.logInWithReadPermissions(
                registryOwner,
                callbackManager,
                listOf("email", "public_profile")
            )
        } else {
            onError(context.getString(R.string.error_facebook_activity_missing))
        }
    }
}

@Composable
fun rememberFacebookSignInHelper(
    onSuccess: (accessToken: String) -> Unit,
    onError: (message: String) -> Unit
): FacebookSignInHelper {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val callbackManager = remember { CallbackManager.Factory.create() }
    val loginManager = remember { LoginManager.getInstance() }
    val errorMessage = stringResource(
        R.string.error_facebook_sign_in_failed
    )
    DisposableEffect(Unit) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                onSuccess(result.accessToken.token)
            }

            override fun onCancel() = Unit

            override fun onError(error: FacebookException) {

                onError(error.localizedMessage ?: errorMessage)
            }
        })
        onDispose { }
    }

    return remember(context, activity, callbackManager, loginManager) {
        FacebookSignInHelper(
            context = context,
            activityResultRegistryOwner = activity as? ActivityResultRegistryOwner,
            callbackManager = callbackManager,
            loginManager = loginManager,
            onError = onError
        )
    }
}