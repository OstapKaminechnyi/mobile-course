package iot.ostapkmn.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_welcome_page.*


class WelcomeActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_page)
        showUserInfo()
        btn_sign_out.setOnClickListener {
            signOut()
        }
    }

    private fun showUserInfo() {
        user = auth.currentUser!!
        val welcomeString = resources.getString(R.string.welcomeString) + user.displayName
        name.text = welcomeString
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}