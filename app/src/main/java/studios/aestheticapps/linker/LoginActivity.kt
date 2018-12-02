package studios.aestheticapps.linker

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener
{
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        signInButton.setOnClickListener(this)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.request_id_token))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
    }

    override fun onStart()
    {
        super.onStart()

        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try
            {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            }
            catch (e: ApiException)
            {
                updateUI(null)
            }
        }
    }

    override fun onClick(v: View)
    {
        val i = v.id
        when (i)
        {
            R.id.signInButton -> signIn()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount)
    {
        showProgressView()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                    showSuccessView()
                    launchMainActivity()
                }
                else
                {
                    updateUI(null)
                    hideProgressView()
                    showErrorView()
                }
            }
    }

    private fun signIn()
    {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut()
    {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
        }
    }

    private fun revokeAccess()
    {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            //updateUI(null)
        }
    }

    private fun updateUI(user: FirebaseUser?)
    {
        if (user != null)
        {
            signInButton.visibility = GONE
            loggingPb.visibility = VISIBLE
        }
        else
        {
            signInButton.visibility = VISIBLE
        }
    }

    private fun launchMainActivity()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showProgressView()
    {
        signInButton.visibility = GONE
        errorTv.visibility = GONE
        loggingPb.visibility = VISIBLE
        signingInfoTv.visibility = VISIBLE
    }

    private fun hideProgressView()
    {
        loggingPb.visibility = GONE
        signingInfoTv.visibility = GONE
    }

    private fun showSuccessView()
    {
        loggingPb.visibility = VISIBLE
        errorTv.visibility = GONE
        signingInfoTv.text = getString(R.string.signin_success)
        signingInfoTv.visibility = VISIBLE
    }

    private fun showErrorView()
    {
        signInButton.visibility = VISIBLE
        errorTv.visibility = VISIBLE
    }

    companion object
    {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 9001
    }
}