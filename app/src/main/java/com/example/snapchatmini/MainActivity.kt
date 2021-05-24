package com.example.snapchatmini

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    var strEmail = ""
    var strPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
    }

    fun login(view: View) {
        strEmail = findViewById<TextView>(R.id.edttxtEmail).text.toString()
        strPassword = findViewById<TextView>(R.id.edttxtPassword).text.toString()

        //val currentUser = FirebaseAuth.getInstance().getCurrentUser()

        //check email already exist or not.
        mAuth!!.fetchSignInMethodsForEmail(strEmail)
            .addOnCompleteListener { task ->
                val isNewUser = task.result!!.getSignInMethods()!!.isEmpty()
                if (isNewUser) {
                    createAccount(strEmail, strPassword);
                } else {
                    signIn(strEmail, strPassword)
                }
            }

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
    }

    fun createAccount(email: String, password: String) {

        try {
            mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = mAuth?.currentUser
                        //#Add user to database
                        val db = FirebaseFirestore.getInstance()

                        // 1.Create a new user with a first and last name
                        val user: MutableMap<String, Any> = HashMap()
                        user["email"] = strEmail
                        val uid = currentUser!!.uid

                        // 2.Add a new document with a generated ID
                        db.collection("users")
                            .document(uid)
                            .set(user)
/*                            .addOnSuccessListener{
                                @Override
                                fun onSuccess(void: Void) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            }
                            .addOnFailureListener{
                                @Override
                                fun onFailure(e: Exception) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            }*/

                            .addOnSuccessListener(OnSuccessListener<Void>()
                            { _ ->
                                Log.d(TAG, "DocumentSnapshot added successfully")
                            })
                            .addOnFailureListener(OnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            })

                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Status", "createUserWithEmail:success")
                        updateUI(currentUser)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Status", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun signIn(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Status", "createUserWithEmail:success")
                    val user = mAuth!!.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Status", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

            }
    }

    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }
}