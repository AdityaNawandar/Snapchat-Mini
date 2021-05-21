package com.example.snapchatmini

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

import kotlin.collections.ArrayList
import android.content.ContentValues.TAG


class UserListActivity : AppCompatActivity() {

    var lvUsers: ListView? = null
    var strCurrentUser = ""
    var mAuth: FirebaseAuth? = null
    lateinit var arrlststrUserEmails: ArrayList<String>
    private lateinit var database: DatabaseReference
    var arrayAdapter: ArrayAdapter<*>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        title = "Users"

        database = Firebase.database.reference
        lvUsers = findViewById(R.id.lstvwUsers)
        arrlststrUserEmails = ArrayList<String>()
        mAuth = FirebaseAuth.getInstance()
        //onStart();
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrlststrUserEmails)

        lvUsers?.adapter = arrayAdapter

        lvUsers!!.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("email", arrlststrUserEmails[position])
            startActivity(intent)
        }
        strCurrentUser = mAuth!!.currentUser!!.email.toString()
        retrieveUsers()

    }

    private fun retrieveUsers() {

        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .get()
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        arrlststrUserEmails.clear()
                        for (document in task.result!!) {

                            arrlststrUserEmails.add(document.data.get("email").toString())
                            lvUsers?.adapter = arrayAdapter
                            //Log.d(TAG, document.id + " => " + document.data)
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getUsersList() {
// Start listing users from the beginning, 1000 at a time.
        // Start listing users from the beginning, 1000 at a time.
/*        var page: ListUsersPage? = FirebaseAuth.getInstance().listUsers(null)
        while (page != null) {
            for (user in page.getValues()) {
                System.out.println("User: " + user.getUid())
            }
            page = page.getNextPage()
        }

// Iterate through all users. This will still retrieve users in batches,
// buffering no more than 1000 users in memory at a time.

// Iterate through all users. This will still retrieve users in batches,
// buffering no more than 1000 users in memory at a time.
        page = FirebaseAuth.getInstance().listUsers(null)
        for (user in page.iterateAll()) {
            System.out.println("User: " + user.getUid())
        }*/

    }


}

