package com.iti.presentation.screens.auth

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.iti.presentation.BuildConfig
import com.iti.presentation.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GoogleSignInHelper(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onSuccess: (idToken: String) -> Unit,
    private val onError: (message: String) -> Unit
) {

    fun signIn() {
        val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        if (clientId.isBlank()) {
            onError(context.getString(R.string.error_google_client_id_missing))
            return
        }

        scope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(clientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = CredentialManager.create(context)
                    .getCredential(request = request, context = context)

                val idToken = GoogleIdTokenCredential
                    .createFrom(result.credential.data)
                    .idToken

                onSuccess(idToken)
            } catch (e: GetCredentialException) {
                onError(e.localizedMessage ?: context.getString(R.string.error_google_sign_in_failed))
            } catch (e: GoogleIdTokenParsingException) {
                onError(e.localizedMessage ?: context.getString(R.string.error_google_sign_in_failed))
            }
        }
    }
}

@Composable
fun rememberGoogleSignInHelper(
    onSuccess: (idToken: String) -> Unit,
    onError: (message: String) -> Unit
): GoogleSignInHelper {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return GoogleSignInHelper(context, scope, onSuccess, onError)
}