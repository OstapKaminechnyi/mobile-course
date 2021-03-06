package iot.ostapkmn.app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import iot.ostapkmn.app.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.btn_sign_up
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private val db = FirebaseFirestore.getInstance()

    private companion object {
        const val PASSWORD_PATTERN = "^[a-zA-Z0-9!@.#$%^&*?_~]{8,16}$"
        const val PHONE_NUMBER_PATTERN = "^\\+380\\d{3}\\d{2}\\d{2}\\d{2}\$"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        btn_sign_up.setOnClickListener {
            signUp()
        }
        btn_sign_in_back.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun signUp() {
        val username = sign_up_name.text.toString()
        val phoneNumber = sign_up_phone_number.text.toString()
        val email = sign_up_email.text.toString()
        val password = sign_up_password.text.toString()
        if (isValidUsername(username) && isValidPhoneNumber(phoneNumber)
                && isValidEmail(email) && isValidPassword(password)) {
            createUser(email, password, username, phoneNumber)
        } else {
            Toast.makeText(baseContext, getString(R.string.tryAgainMessage),
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUser(email: String, password: String, name: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        handleUserCreating(task, email, name, phone)
                    } else {
                        Toast.makeText(
                                baseContext, getString(R.string.createUserError),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
    }

    private fun authenticateUser(name: String) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    handleUserAuthentication(user, task)
                }
    }

    private fun handleUserAuthentication(user: FirebaseUser, task: Task<Void>) {
        if (task.isSuccessful) {
            Toast.makeText(
                    this, getString(R.string.successfulSignUp) + "," + user.displayName,
                    Toast.LENGTH_LONG
            ).show()
            val intent = Intent(this, TabActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun handleUserCreating(
            task: Task<AuthResult>,
            email: String,
            name: String,
            phone: String
    ) {
        if (task.isSuccessful) {
            addUserToDB(auth.currentUser!!.uid, email, name, phone)
            authenticateUser(name)
        } else {
            Toast.makeText(
                    baseContext, getString(R.string.createUserError),
                    Toast.LENGTH_SHORT
            ).show()        }
    }

    private fun addUserToDB(uid: String, email: String, name: String, phone: String) {
        val user = hashMapOf<String, Any>(
                "email" to email,
                "name" to name,
                "phone" to phone,
                "photoURL" to ""
        )
        db.collection("users")
                .document(uid)
                .set(user)
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
        Toast.makeText(this, getString(R.string.successfulSignUp), Toast.LENGTH_LONG).show()
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return if (sign_up_email.text.toString().isEmpty()) {
            sign_up_email.error = getString(R.string.emailError)
            false
        } else pattern.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern: Pattern = Pattern.compile(PASSWORD_PATTERN)
        return if (sign_up_password.text.toString().isEmpty()) {
            sign_up_password.error = getString(R.string.passwordError)
            false
        } else passwordPattern.matcher(password).matches()
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phonePattern: Pattern = Pattern.compile(PHONE_NUMBER_PATTERN)
        return if (sign_up_phone_number.text.toString().isEmpty()) {
            sign_up_phone_number.error = getString(R.string.phoneNumberError)
            false
        } else phonePattern.matcher(phoneNumber).matches()
    }

    private fun isValidUsername(username: String): Boolean {
        return if (sign_up_name.text.toString().isEmpty()) {
            sign_up_name.error = getString(R.string.usernameError)
            false
        } else return true
    }

}