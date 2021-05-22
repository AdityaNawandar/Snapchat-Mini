package com.example.snapchatmini

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class UserListActivity : AppCompatActivity() {

    var lvUsers: ListView? = null
    var strCurrentUser = ""
    var mAuth: FirebaseAuth? = null
    lateinit var arrlststrUserEmails: ArrayList<String>
    lateinit var arrlststrKeys: ArrayList<String>
    private lateinit var database: DatabaseReference
    var arrayAdapter: ArrayAdapter<*>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        title = "Users"
        lvUsers = findViewById(R.id.lstvwUsers)
        arrlststrUserEmails = ArrayList<String>()
        arrlststrKeys = ArrayList<String>()

        try {//Firebase
            database = Firebase.database.reference
            mAuth = FirebaseAuth.getInstance()

            strCurrentUser = mAuth?.currentUser?.email.toString()

            //Adapter
            arrayAdapter =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, arrlststrUserEmails)
            lvUsers?.adapter = arrayAdapter

            retrieveUsers()
            //checkDBConnection()

            lvUsers?.setOnItemClickListener { parent, view, position, id ->

                val db = FirebaseFirestore.getInstance()
                var data = intent
                var imageName = data.getStringExtra("imageName")!!
                var imageURL = data.getStringExtra("imageURL")!!
                var message = data.getStringExtra("message")!!

                var toUser = arrlststrKeys.get(position)

                // Create a new snaps collection for the selected user
                val snapMap: MutableMap<String, Any> = HashMap()
                snapMap["from"] = strCurrentUser
                snapMap["imageName"] = imageName
                snapMap["imageURL"] = imageURL
                snapMap["message"] = message


                db.collection("users").document(toUser).collection("snaps")
                    .add(snapMap)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG,"DocumentSnapshot added with ID: " + documentReference.id)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG,"Error adding document",e)
                    }


/*                val snapMap = hashMapOf(
                    "from" to strCurrentUser,
                    "imageName" to imageName,
                    "imageURL" to imageURL,
                    "message" to message
                )
                val selectedUser = FirebaseDatabase.getInstance().reference.child("users")
                    .child(arrlststrKeys.get(position))
                val output = selectedUser.child("snaps").push().setValue(snapMap)
                val docRef = db.collection("users").document(selectedUser.toString()).
                    .child("snaps")
                    .set("snaps")
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }*/

                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkDBConnection() {

        //var connected:
        val connectedRef = Firebase.database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)
                if (connected!!) {
                    Log.d(TAG, "connected")
                } else {
                    Log.d(TAG, "not connected")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Listener was cancelled")
            }
        });
        //return connected
    }

    private fun retrieveUsers() {

        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        arrlststrUserEmails.clear()
                        arrlststrKeys.clear()
                        for (document in task.result!!) {
                            arrlststrUserEmails.add(document.data.get("email").toString())
                            arrlststrKeys.add(document.id)
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

}

