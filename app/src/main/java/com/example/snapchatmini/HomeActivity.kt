package com.example.snapchatmini

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.content.ContentValues.TAG
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

class HomeActivity : AppCompatActivity() {


    lateinit var lstvwSnaps: ListView
    var mAuth: FirebaseAuth? = null
    lateinit var arrlststrEmails: ArrayList<String>
    var arrayAdapter: ArrayAdapter<*>? = null
    lateinit var arrlstSnaps: ArrayList<QueryDocumentSnapshot>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        arrlststrEmails = ArrayList()
        arrlstSnaps = ArrayList()
        title = "Home"
        lstvwSnaps = findViewById(R.id.lstvwSnaps)
        mAuth = FirebaseAuth.getInstance()

        getSnaps()

        lstvwSnaps.setOnItemClickListener { parent, view, position, id ->

            intent = Intent(this, ViewSnapActivity::class.java)
            intent.putExtra("imageName", arrlstSnaps.get(position).data["imageName"].toString())
            intent.putExtra("imageURL", arrlstSnaps.get(position).data["imageURL"].toString())
            intent.putExtra("message", arrlstSnaps.get(position).data["message"].toString())
            intent.putExtra("snapID", arrlstSnaps.get(position).id)
            startActivity(intent)

        }
    }

    private fun getSnaps() {

        try {
            //Fetch snaps

            var currentUser = mAuth?.currentUser
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(currentUser!!.uid).collection("snaps")
                .get()
                .addOnSuccessListener { documents ->
                    //arrlststrEmails.clear()
                    this.arrlststrEmails.clear();
                    for (document in documents) {
                        arrlststrEmails.add(document.data["from"].toString())

                        Log.d(TAG, "emails: " + document.data["from"].toString())
                        arrlstSnaps.add(document)
                    }
                    arrayAdapter =
                        ArrayAdapter(this, android.R.layout.simple_list_item_1, arrlststrEmails)
                    lstvwSnaps.adapter = arrayAdapter
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //#Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_snap, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.newsnap) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                getPhoto()
            }
        } else if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    private fun getPhoto() {
        val intent = Intent(applicationContext, SnapActivity::class.java)
        startActivity(intent)
    }

}