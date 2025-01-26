package com.example.schetodo.feature.authentication

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.schetodo.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationService @Inject constructor(
    private val context: Context,
) {

    suspend fun signInWithGoogle() {
        val nonce = "TODO" // TODO: Generate nonce
        val credential = launchGoogleSignInBottomSheet(nonce)

        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            Log.e(TAG, "signInWithGoogle: Received invalid credential. Type: ${credential.type}")
            throw GoogleSignInException()
        }

        try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Log.d(TAG, "signInWithGoogle: Received google id token: ${googleIdTokenCredential.idToken}")
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "signInWithGoogle: Error parsing GoogleIdTokenCredential", e)
            throw GoogleSignInException()
        }
    }

    private suspend fun launchGoogleSignInBottomSheet(nonce: String): Credential {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.OAUTH_WEB_CLIENT_ID)
                .setNonce(nonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = CredentialManager.create(context).getCredential(
                request = request,
                context = context
            )

            return result.credential
        } catch (e: NoCredentialException) {
            Log.i(TAG, "No credential found", e)
            throw GoogleSignInNoCredentialException()
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Error getting credential", e)
            throw GoogleSignInException()
        }
    }

    private companion object {
        private const val TAG = "AuthenticationService"
    }

    class GoogleSignInNoCredentialException : Exception("No google credentials found")
    class GoogleSignInException: Exception("Error while signing in with Google")
}
